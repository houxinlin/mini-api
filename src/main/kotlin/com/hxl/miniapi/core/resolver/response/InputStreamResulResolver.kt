package com.hxl.miniapi.core.resolver.response

import com.hxl.miniapi.core.ResultResolver
import com.hxl.miniapi.http.ContentType
import java.io.InputStream

class InputStreamResulResolver: ResultResolver() {
    override fun support(data: Any?): Boolean {
        return data!=null && ( data is ByteArray ||  data is InputStream)
    }

    override fun resolverValue(data: Any?): ByteArray {
        if (data is ByteArray) return  data
        if (data is InputStream) return  data.readBytes()
        return null!!
    }

    override fun getContentType(data: Any?): ContentType {
        return ContentType.APPLICATION_STREAM
    }
}