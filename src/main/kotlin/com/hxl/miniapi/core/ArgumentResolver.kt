package com.hxl.miniapi.core

import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse


/**
* @description: 参数解析器
* @date: 2022/10/5 上午5:45
*/

interface ArgumentResolver {
    fun support(parameterInfo: MethodParameter, request: HttpRequest):Boolean

    fun resolver(parameterInfo: MethodParameter, request: HttpRequest,response:HttpResponse, mappingInfo: MappingInfo):Any?
}