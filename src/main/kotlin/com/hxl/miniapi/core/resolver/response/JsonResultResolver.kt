package com.hxl.miniapi.core.resolver.response

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.JsonConvert
import com.hxl.miniapi.core.ResultResolver
import com.hxl.miniapi.http.ContentType
import com.hxl.miniapi.utils.isBaseType
import com.hxl.miniapi.utils.isString

class JsonResultResolver(private val context: Context):ResultResolver() {
    override fun support(data: Any?): Boolean {
        return data!=null &&( !(data::class.java.isString() || data::class.java.isBaseType()))
    }

    override fun resolverValue(data: Any?): ByteArray {
        return context.getJsonConvert()!!.toJson(data!!)!!.toByteArray()
    }

    override fun getContentType(data: Any?): ContentType {
        return ContentType.APPLICATION_JSON
    }
}