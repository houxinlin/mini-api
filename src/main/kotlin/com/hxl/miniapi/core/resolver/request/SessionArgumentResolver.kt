package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse
import com.hxl.miniapi.http.session.Session

class SessionArgumentResolver:ArgumentResolver {
    override fun support(parameterInfo: MethodParameter, request: HttpRequest): Boolean {
        return Session::class.java.isAssignableFrom(parameterInfo.param.type)
    }

    override fun resolver(
        parameterInfo: MethodParameter,
        request: HttpRequest,
        response: HttpResponse,
        mappingInfo: MappingInfo
    ): Any {
        return request.getSession()
    }
}