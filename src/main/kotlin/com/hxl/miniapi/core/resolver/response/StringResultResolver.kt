package com.hxl.miniapi.core.resolver.response

import com.hxl.miniapi.core.ResultResolver
import com.hxl.miniapi.http.ContentType
import com.sun.net.httpserver.HttpExchange

class StringResultResolver:ResultResolver() {
    override fun support(data: Any?): Boolean {
        return data!=null &&data is String
    }

    override fun resolverValue(data: Any): ByteArray {
        return data.toString().toByteArray()
    }

    override fun getContentType(): ContentType {
        return ContentType.TEXT_PLAIN
    }
}