package com.hxl.miniapi.core.resolver.response

import com.hxl.miniapi.core.ResultResolver
import com.hxl.miniapi.http.ContentType
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * 字节流，默认是进行下载，如果想浏览器进行数据显示，需要手动设置header
 */
class ByteStreamResulResolver : ResultResolver() {
    override fun support(data: Any?): Boolean {
        return data != null && (data is ByteArray ||
                data is ByteArrayOutputStream||
                data is InputStream)
    }

    override fun resolverValue(data: Any?): ByteArray {
        if (data is ByteArray) return data
        if (data is InputStream) return data.readBytes()
        if (data is ByteArrayOutputStream) return data.toByteArray()
        return null!!
    }

    override fun getContentType(data: Any?): ContentType {
        return ContentType.APPLICATION_STREAM
    }
}