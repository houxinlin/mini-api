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

    open fun getStatusCode(data: Any?): Int {
        return HttpStatus.SUCCESS.code
    }

    /**
     * @description: 转换
     * @date: 2022/10/3 下午9:02
     */
    fun resolver(data: Any?, httpExchange: HttpExchange) {
        var value = resolverValue(data)
        if (value == null) value = ByteArray(0)

        httpExchange.responseHeaders.set("Content-Type", applyCharset(getContentType(data).contentType))
        httpExchange.responseHeaders.set("Content-Length", value.size.toString())
        httpExchange.sendResponseHeaders(getStatusCode(data), value.size.toLong())
        httpExchange.responseBody.run {
            write(value)
            close()
        }
    }

    private fun applyCharset(contentType: String):String{
        if (contentType.endsWith(";"))return "$contentType charset=utf-8"
        return "$contentType; charset=utf-8"
    }

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
}