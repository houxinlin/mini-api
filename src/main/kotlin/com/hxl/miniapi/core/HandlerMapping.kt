package com.hxl.miniapi.core

import com.hxl.miniapi.http.HttpRequestAdapter
import com.sun.net.httpserver.HttpExchange

fun interface HandlerMapping {

    /**
    * @description: 处理请求
    * @date: 2022/10/1 下午12:49
    */

    fun handler(httpRequestAdapter: HttpRequestAdapter):Any
}