package com.hxl.miniapi.core

import com.hxl.miniapi.core.exception.HttpExceptionUtils
import com.hxl.miniapi.http.model.Model
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Parameter

class HandlerMappingImpl(private val mappingInfo: MappingInfo, private val context: Context) : HandlerMapping {
    private val parameterInfos = getMethodParameters()

    /**
     * @description: 处理请求，并返回用户处理的结果
     * @date: 2022/10/5 下午7:10
     */

    override fun handler(httpRequest: HttpRequest, httpResponse: HttpResponse): Model {
        return invokeUserInterface(httpRequest, httpResponse)
    }

    /**
     * @description: 调用用户接口
     * @date: 2022/10/5 下午7:11
     */

    private fun invokeUserInterface(httpRequest: HttpRequest, httpResponse: HttpResponse): Model {
        val methodArg = mutableListOf<Any?>()
        for (parameterInfo in parameterInfos) {
            val argumentResolvers = findArgumentResolvers(parameterInfo, httpRequest)
                ?: throw HttpExceptionUtils.create500("找不到参数解析器 ${parameterInfo.parameterName} ${parameterInfo.param.type}")
            methodArg.add(argumentResolvers.resolver(parameterInfo, httpRequest, httpResponse, mappingInfo))
        }
        val invokeResult: Any?
        try {
            invokeResult = if (mappingInfo.method.parameterCount == 0) {
                mappingInfo.method.invoke(mappingInfo.instance)
            } else {
                mappingInfo.method.invoke(mappingInfo.instance, *methodArg.toTypedArray())
            }
        } catch (e: InvocationTargetException) {
            return Model().apply { this.data = e.targetException }
        }
        return Model().apply { this.data = invokeResult }
    }


    private fun findArgumentResolvers(parameter: MethodParameter, httpRequest: HttpRequest): ArgumentResolver? {
        for (argumentResolver in context.getArgumentResolvers()) {
            //找到能处理这个参数的参数解析器
            if (argumentResolver.support(parameter, httpRequest)) return argumentResolver
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