package com.hxl.miniapi.core

import com.hxl.miniapi.http.HttpRequestAdapter


/**
* @description: 参数解析器
* @date: 2022/10/5 上午5:45
*/

interface ArgumentResolver {
    fun support(parameterInfo: MethodParameter, request: HttpRequestAdapter):Boolean

    fun resolver(parameterInfo: MethodParameter, request: HttpRequestAdapter, mappingInfo: MappingInfo):Any?
}