package com.hxl.miniapi.core

import com.hxl.miniapi.http.HttpRequestAdapter

interface ArgumentResolver {
    fun support(parameterInfo: MethodParameter, request: HttpRequestAdapter):Boolean

    fun resolver(parameterInfo: MethodParameter, request: HttpRequestAdapter, mappingInfo: MappingInfo):Any?
}