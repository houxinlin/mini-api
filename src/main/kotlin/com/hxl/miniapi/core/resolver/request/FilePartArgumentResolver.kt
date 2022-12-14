package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.core.exception.HttpExceptionUtils
import com.hxl.miniapi.http.anno.param.RequestParam
import com.hxl.miniapi.http.file.FilePart
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse
import kotlin.reflect.full.isSuperclassOf

/**
* @description: 解析FilePart
* @date: 2022/10/5 上午5:46
*/

class FilePartArgumentResolver :ArgumentResolver{
    override fun support(parameterInfo: MethodParameter, request: HttpRequest): Boolean {
        return parameterInfo.param.type.kotlin==FilePart::class || List::class.isSuperclassOf(parameterInfo.param.type.kotlin)
    }

    override fun resolver(
        parameterInfo: MethodParameter,
        request: HttpRequest,
        response: HttpResponse,
        mappingInfo: MappingInfo
    ): Any {
        val argumentName = if (parameterInfo.hasAnnotation(RequestParam::class.java)) {
            parameterInfo.getAnnotation(RequestParam::class.java)!!.value
        } else parameterInfo.parameterName
        if (List::class.isSuperclassOf(parameterInfo.param.type.kotlin)) {
            return request.listFile()
        }
        return request.getFile(argumentName) ?: throw HttpExceptionUtils.create400("找不到参数${argumentName}")
    }
}