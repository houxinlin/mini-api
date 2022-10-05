package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.http.HttpRequestAdapter
import com.hxl.miniapi.http.session.Session

class SessionArgumentResolver:ArgumentResolver {
    override fun support(parameterInfo: MethodParameter, request: HttpRequestAdapter): Boolean {
        return parameterInfo.param.type==Session::class.java
    }

    override fun resolver(parameterInfo: MethodParameter, request: HttpRequestAdapter, mappingInfo: MappingInfo): Any? {
        return request.getSession()
    }
}