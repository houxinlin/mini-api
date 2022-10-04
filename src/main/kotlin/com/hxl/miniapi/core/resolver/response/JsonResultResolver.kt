package com.hxl.miniapi.core.resolver.response

import com.hxl.miniapi.core.JsonConvert
import com.hxl.miniapi.core.ResultResolver
import com.hxl.miniapi.http.ContentType
import com.hxl.miniapi.utils.isBaseType
import com.hxl.miniapi.utils.isString
import com.sun.net.httpserver.HttpExchange

class JsonResultResolver(private val jsonConvert: JsonConvert):ResultResolver() {
    override fun support(data: Any?): Boolean {
        return data!=null &&( !(data::class.java.isString() || data::class.java.isBaseType()))
    }

    override fun resolverValue(data: Any): ByteArray {
        return jsonConvert.toJson(data)!!.toByteArray()
    }

    override fun getContentType(): ContentType {
        return ContentType.APPLICATION_JSON
    }
}