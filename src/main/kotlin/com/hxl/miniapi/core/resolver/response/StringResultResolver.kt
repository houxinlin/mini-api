package com.hxl.miniapi.core.resolver.response

import com.hxl.miniapi.core.ResultResolver
import com.hxl.miniapi.http.ContentType

class StringResultResolver : ResultResolver() {
    override fun support(data: Any?): Boolean {
        return data != null && data is String
    }

    override fun resolverValue(data: Any?): ByteArray {
        return data.toString().toByteArray()
    }

    override fun getContentType(data: Any?): ContentType {
        return ContentType.TEXT_PLAIN
    }
}