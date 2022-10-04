package com.hxl.miniapi.http

import com.hxl.miniapi.http.file.FilePart
import com.sun.net.httpserver.HttpExchange

class HttpMultipartAdapter(private val httpExchange: HttpExchange) : HttpRequestAdapter(httpExchange) {
    init {
    }
    fun getBoundary(): String {
        val contentType = httpExchange.requestHeaders["Content-Type"]!![0]
        return "--${contentType.substring(contentType.indexOf("boundary=") + 9)}"
    }
    fun getFilePart(argumentName: String):FilePart?{
        return MultipartParser(requestBody, getBoundary()).getFiles().find { it.name ==argumentName }
    }
}