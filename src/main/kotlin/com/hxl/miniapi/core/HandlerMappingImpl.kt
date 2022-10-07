package com.hxl.miniapi.core

import com.hxl.miniapi.http.HttpRequestAdapter
import com.hxl.miniapi.http.InterceptResponse
import com.hxl.miniapi.http.NothingResponse
import java.lang.reflect.Parameter

class HandlerMappingImpl(private val mappingInfo: MappingInfo, private val context: Context) : HandlerMapping {
    private val parameterInfos = getMethodParameters()

    /**
     * @description: 处理请求，并返回用户处理的结果
     * @date: 2022/10/5 下午7:10
     */

    override fun handler(httpRequestAdapter: HttpRequestAdapter): Any {
        return invokeUserInterface(httpRequestAdapter)
    }
    /**
     * @description: 调用用户接口
     * @date: 2022/10/5 下午7:11
     */

    private fun invokeUserInterface(requestAdapter: HttpRequestAdapter): Any {
        //如果有拦截器拦截了此请求
        val httpIntercept = context.getHttpIntercept().find { it.intercept(requestAdapter) }
        if (httpIntercept != null) {
            httpIntercept.postHandler(requestAdapter)
            return InterceptResponse()
        }
        val methodArg = mutableListOf<Any?>()
        for (parameterInfo in parameterInfos) {
            val argumentResolvers = findArgumentResolvers(parameterInfo, requestAdapter)
                ?: throw IllegalStateException("找不到参数解析器 ${parameterInfo.parameterName} ${parameterInfo.param.type}")
            methodArg.add(argumentResolvers.resolver(parameterInfo, requestAdapter, mappingInfo))
        }
        val invokeResult: Any? = if (mappingInfo.method.parameterCount == 0) {
            mappingInfo.method.invoke(mappingInfo.instance)
        } else {
            mappingInfo.method.invoke(mappingInfo.instance, *methodArg.toTypedArray())
        }
        //如果没有返回值&&null
        if (mappingInfo.method.returnType.kotlin == Void::class || invokeResult == null) return NothingResponse()
        return invokeResult
    }


    private fun findArgumentResolvers(
        parameter: MethodParameter,
        requestAdapter: HttpRequestAdapter
    ): ArgumentResolver? {
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