package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.core.convert.SimpleTypeConverter
import com.hxl.miniapi.core.exception.HttpExceptionUtils
import com.hxl.miniapi.http.anno.param.PathVariable
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse
import com.hxl.miniapi.utils.isBaseType
import com.hxl.miniapi.utils.isString


/**
 * @description: 解析@PathVariable变量
 * @date: 2022/10/5 上午5:59
 */

class PathVariableArgumentResolver : ArgumentResolver {
    companion object {
        val VARIABLE_REGEX = "^\\{(.+)}$".toRegex()
    }

    private val simpleTypeConverter = SimpleTypeConverter()
    override fun support(parameterInfo: MethodParameter, request: HttpRequest): Boolean {
        return parameterInfo.hasAnnotation(PathVariable::class.java) &&
                (parameterInfo.param.type.isString() || parameterInfo.param.type.isBaseType())
    }

    override fun resolver(
        parameterInfo: MethodParameter,
        request: HttpRequest,
        response: HttpResponse,
        mappingInfo: MappingInfo
    ): Any? {
        //要获取的变量值名
        val pathVariableValue = if (parameterInfo.hasAnnotation(PathVariable::class.java)) {
            parameterInfo.getAnnotation(PathVariable::class.java)!!.value
        } else parameterInfo.parameterName

        val requestPathSplit = request.getRequestPath().split("/")
        val patternsUrlSplite = mappingInfo.urlPatterns.split("/")

        if (requestPathSplit.size != patternsUrlSplite.size) return null
        for (i in patternsUrlSplite.indices) {
            //如果符合转换条件
            if (VARIABLE_REGEX.matches(patternsUrlSplite[i]) &&
                VARIABLE_REGEX.find(patternsUrlSplite[i])!!.groupValues[1] == pathVariableValue
            ) {
                return simpleTypeConverter.typeConvert(parameterInfo.param.type, requestPathSplit[i])
            }
        }
        throw HttpExceptionUtils.create400("参数${pathVariableValue}无法转换")
    }
}