package com.hxl.miniapi.core

import com.hxl.miniapi.http.ContentType
import com.hxl.miniapi.http.HttpMultipartAdapter
import com.hxl.miniapi.http.HttpRequestAdapter
import com.hxl.miniapi.utils.startWhithPlus
import com.sun.net.httpserver.HttpExchange
import java.lang.reflect.Parameter

class HandlerMappingImpl(private val mappingInfo: MappingInfo, private val context: Context) : HandlerMapping {
    private val parameterInfos = getMethodParameters()
    override fun handler(http: HttpExchange): Any {
        val methodArg = mutableListOf<Any?>()
        //遍历所有参数
        val requestAdapter =if (isFormDataRequest(http)) HttpMultipartAdapter(http) else HttpRequestAdapter(http)
        for (parameterInfo in parameterInfos) {
            val argumentResolvers = findArgumentResolvers(parameterInfo, requestAdapter) ?: throw IllegalStateException("没有找到参数解析器")
            methodArg.add(argumentResolvers.resolver(parameterInfo, requestAdapter, mappingInfo))
        }
        val result: Any? = if (mappingInfo.method.parameterCount == 0) {
            mappingInfo.method.invoke(mappingInfo.instance)
        } else {
            mappingInfo.method.invoke(mappingInfo.instance, *methodArg.toTypedArray())
        }
        return result!!
    }
    private fun isFormDataRequest(httpExchange: HttpExchange):Boolean{
        val result  = httpExchange.requestHeaders["Content-Type"]
            ?.find { it.startWhithPlus(ContentType.FORM_DATA.contentType) }
        return  result!= null
    }

    private fun findArgumentResolvers(parameter: MethodParameter, requestAdapter: HttpRequestAdapter): ArgumentResolver? {
        for (argumentResolver in context.getArgumentResolvers()) {
            //找到能处理这个参数的参数解析器
            if (argumentResolver.support(parameter, requestAdapter)) {
                return argumentResolver
            }
        }
        return null
    }

    private fun getMethodParameters(): List<MethodParameter> {
        val result = mutableListOf<MethodParameter>()
        val parameters = mappingInfo.method.parameters
        parameters.forEach { result.add(getParameters(it)) }
        return result
    }

    private fun getParameters(param: Parameter): MethodParameter {
        return MethodParameter().apply {
            this.method = mappingInfo.method
            this.parameterName = param.name
            this.parameterAnnotations.addAll(param.declaredAnnotations)
            this.param = param
        }
    }
}