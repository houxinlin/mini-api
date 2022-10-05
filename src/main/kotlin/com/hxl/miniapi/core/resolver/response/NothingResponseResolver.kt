package com.hxl.miniapi.core.resolver.response

import com.hxl.miniapi.core.ResultResolver
import com.hxl.miniapi.http.ContentType
import com.hxl.miniapi.http.NothingResponse

class NothingResponseResolver:ResultResolver() {
    override fun support(data: Any?): Boolean {
        return data is NothingResponse
    }

    override fun resolverValue(data: Any): ByteArray {
        return ByteArray(0)
    }

    override fun getContentType(): ContentType {
        return ContentType.TEXT_PLAIN
    }
}