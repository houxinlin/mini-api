package com.hxl.miniapi.http

import com.hxl.miniapi.core.exception.ClientException
import com.hxl.miniapi.http.file.FilePart
import com.sun.net.httpserver.HttpExchange

class HttpMultipartAdapter(private val httpExchange: HttpExchange) : HttpRequestAdapter(httpExchange) {
    private val multipartParser = MultipartParser(requestBody, getBoundary())
    fun getBoundary(): String {
        val contentTypeValue = getStringContentType() ?: throw ClientException.create400("客户端请求格式错误")
        return "--${contentTypeValue.substring(contentTypeValue.indexOf("boundary=")+9)}"
    }

    fun getFilePart(argumentName: String): FilePart? {
        return multipartParser.getFiles().find { it.name == argumentName }
    }
}