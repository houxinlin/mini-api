package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.core.exception.ClientException
import com.hxl.miniapi.http.HttpMultipartAdapter
import com.hxl.miniapi.http.HttpRequestAdapter
import com.hxl.miniapi.http.anno.param.RequestParam
import com.hxl.miniapi.http.file.FilePart

class FilePartArgumentResolver :ArgumentResolver{
    override fun support(parameterInfo: MethodParameter, request: HttpRequestAdapter): Boolean {
        return parameterInfo.param.type.kotlin==FilePart::class
    }

    override fun resolver(parameterInfo: MethodParameter, request: HttpRequestAdapter, mappingInfo: MappingInfo): Any? {
        val argumentName = if (parameterInfo.hasAnnotation(RequestParam::class.java)){
            parameterInfo.getAnnotation(RequestParam::class.java)!!.value
        } else parameterInfo.parameterName

        if (request is HttpMultipartAdapter ) {
            return request.getFilePart(argumentName) ?: throw ClientException("找不到文件", 400)
        }
        return  ClientException("找不到文件",400)
    }
}