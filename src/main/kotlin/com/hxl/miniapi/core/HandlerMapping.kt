package com.hxl.miniapi.core

import com.hxl.miniapi.http.model.Model
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse

fun interface HandlerMapping {

    /**
    * @description: 处理请求
    * @date: 2022/10/1 下午12:49
    */

    fun handler(httpRequest: HttpRequest, httpResponse: HttpResponse):Model
}