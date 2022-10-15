package com.hxl.miniapi.core

import com.hxl.miniapi.http.ContentType
import com.hxl.miniapi.http.HttpStatus
import com.hxl.miniapi.http.response.HttpResponse
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
     * @description: 交给具体子类
     * @date: 2022/10/5 上午4:31
     */
    protected abstract fun resolverValue(data: Any?): ByteArray?

    /**
     * @description: 子类所要求的类型
     * @date: 2022/10/5 上午4:31
     */

    abstract fun getContentType(data: Any?): ContentType

    open fun getStatusCode(data: Any?): Int {
        return HttpStatus.SUCCESS.code
    }

    /**
     * @description: 转换
     * @date: 2022/10/3 下午9:02
     */
    fun resolver(data: Any?, httpExchange: HttpExchange, httpResponse: HttpResponse) {
        var value = resolverValue(data)
        if (value == null) value = ByteArray(0)
        val userContentType = httpResponse.getContentType()
        val userStatus = httpResponse.getStatus()
        val status =if (userStatus ==0) getStatusCode(data) else userStatus
        httpExchange.responseHeaders.set("Content-Type",userContentType ?: getContentType(value).applyCharset("utf-8"))
        if (value.isNotEmpty())httpExchange.responseHeaders.set("Content-Length", value.size.toString())
        httpExchange.sendResponseHeaders(status, value.size.toLong())
        //结束一次请求
        httpExchange.responseBody.run {
            write(value)
            close()
        }
    }
}