package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.core.convert.SimpleTypeConverter
import com.hxl.miniapi.http.HttpRequestAdapter
import com.hxl.miniapi.http.anno.param.PathVariable

class PathVariableArgumentResolver : ArgumentResolver {
    companion object {
        val VARIABLE_REGEX = "^\\{(.+)}$".toRegex()
    }

    private val simpleTypeConverter = SimpleTypeConverter()
    override fun support(parameterInfo: MethodParameter, request: HttpRequestAdapter): Boolean {
        return parameterInfo.hasAnnotation(PathVariable::class.java)
    }

    override fun resolver(parameterInfo: MethodParameter, request: HttpRequestAdapter, mappingInfo: MappingInfo): Any? {
        val requestPathSplit = request.getRequestPath().split("/")
        //要获取的变量值名
        val pathVariableValue = parameterInfo.getAnnotation(PathVariable::class.java)!!.value
        val patternsUrlSplite = mappingInfo.urlPatterns.split("/")
        if (requestPathSplit.size != patternsUrlSplite.size) return null
        for (i in patternsUrlSplite.indices) {
            if (VARIABLE_REGEX.matches(patternsUrlSplite[i]) &&
                VARIABLE_REGEX.find(patternsUrlSplite[i])!!.groupValues[1] == pathVariableValue) {
                return simpleTypeConverter.typeConvert(parameterInfo.param.type, requestPathSplit[i])
            }
        }
        return null
    }
}