package com.hxl.miniapi.core.resolver.response

import com.hxl.miniapi.core.ResultResolver
import com.hxl.miniapi.http.ContentType

class NullResultResolver:ResultResolver() {
    override fun support(data: Any?): Boolean {
        return data== null
    }
    override fun resolverValue(data: Any?): ByteArray {
       return  ByteArray(0)
    }
    override fun getContentType(data: Any?): ContentType {
        return ContentType.TEXT_PLAIN
    }
}