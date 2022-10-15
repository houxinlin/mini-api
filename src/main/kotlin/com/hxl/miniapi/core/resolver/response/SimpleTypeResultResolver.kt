package com.hxl.miniapi.core.resolver.response

import com.hxl.miniapi.core.ResultResolver
import com.hxl.miniapi.http.ContentType
import com.hxl.miniapi.utils.isBaseType

class SimpleTypeResultResolver :ResultResolver(){
    override fun support(data: Any?): Boolean {
        return data!=null && data::class.java.isBaseType()
    }
    override fun resolverValue(data: Any?): ByteArray {
        return data.toString().toByteArray()
    }
    override fun getContentType(data: Any?): ContentType {
        return ContentType.TEXT_PLAIN
    }
}