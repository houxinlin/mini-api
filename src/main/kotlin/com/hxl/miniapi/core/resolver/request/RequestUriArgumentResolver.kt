package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.http.HttpRequestAdapter
import com.hxl.miniapi.http.anno.param.RequestUri
import com.hxl.miniapi.utils.getDefaultValue
import com.hxl.miniapi.utils.isBaseType
import com.hxl.miniapi.utils.isString

class RequestUriArgumentResolver: ArgumentResolver {
    override fun support(parameterInfo: MethodParameter, request: HttpRequestAdapter): Boolean {
        return  parameterInfo.hasAnnotation(RequestUri::class.java)
    }

    override fun resolver(parameterInfo: MethodParameter, request: HttpRequestAdapter, mappingInfo: MappingInfo): Any? {
        if (parameterInfo.param.type.isString()){
            return request.getRequestPath()
        }
        if (parameterInfo.param.type.isBaseType()){
            return parameterInfo.param.type.getDefaultValue()
        }
        return null

    }
}