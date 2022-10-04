package com.hxl.miniapi.core

import com.hxl.miniapi.http.ContentType
import com.hxl.miniapi.http.HttpStatus
import com.sun.net.httpserver.HttpExchange

/**
 * @description: 用于把对象转换为可返回的数据类型
 * @date: 2022/10/3 下午9:02
 */

abstract class ResultResolver {

    /**
     * @description: 是否支持
     * @date: 2022/10/3 下午9:01
     */

    abstract fun support(data: Any?): Boolean


    /**
     * @description: 转换
     * @date: 2022/10/3 下午9:02
     */
    fun resolver(data: Any, httpExchange: HttpExchange) {
        val value = resolverValue(data)
        httpExchange.sendResponseHeaders(HttpStatus.SUCCESS.code, value.size.toLong())
        httpExchange.responseHeaders.set("Content-Type", getContentType().contentType)
        httpExchange.responseHeaders.set("Content-Length",value.size.toString())
        httpExchange.responseBody.run {
            write(value)
            close()
        }
    }


    /**
     * @description: 交给具体子类
     * @date: 2022/10/5 上午4:31
     */
    protected abstract fun resolverValue(data: Any): ByteArray


    /**
     * @description: 子类所要求的类型
     * @date: 2022/10/5 上午4:31
     */

    abstract fun getContentType(): ContentType
}