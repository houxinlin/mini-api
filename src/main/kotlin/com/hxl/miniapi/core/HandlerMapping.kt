package com.hxl.miniapi.core

import com.sun.net.httpserver.HttpExchange

fun interface HandlerMapping {

    /**
    * @description: 处理请求
    * @date: 2022/10/1 下午12:49
    */

    fun handler(http: HttpExchange):Any
}