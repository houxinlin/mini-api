package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse

class RequestRawParamResolver : ArgumentResolver {
    override fun support(parameterInfo: MethodParameter, request: HttpRequest): Boolean {
        return parameterInfo.param.type == HttpRequest::class.java ||
                parameterInfo.param.type == HttpResponse::class.java
    }

    override fun resolver(
        parameterInfo: MethodParameter,
        request: HttpRequest,
        response: HttpResponse,
        mappingInfo: MappingInfo
    ): Any {
        return if (parameterInfo.param.type == HttpRequest::class.java) request else response
    }
}