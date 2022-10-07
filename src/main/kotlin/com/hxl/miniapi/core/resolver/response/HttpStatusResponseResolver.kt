package com.hxl.miniapi.core.resolver.response

import com.hxl.miniapi.core.JsonConvert
import com.hxl.miniapi.core.ResultResolver
import com.hxl.miniapi.http.BaseHttpResponse
import com.hxl.miniapi.http.BeanHttpResponse
import com.hxl.miniapi.http.ContentType
import com.hxl.miniapi.utils.isBaseType
import com.hxl.miniapi.utils.isString

/**
* @description: 处理BaseHttpResponse结果
* @date: 2022/10/5 上午11:02
*/

class HttpStatusResponseResolver(private val jsonConvert: JsonConvert) : ResultResolver() {
    override fun support(data: Any?): Boolean {
        return data!=null && data is BaseHttpResponse
    }

    override fun getStatusCode(data: Any?): Int {
        if (data is BaseHttpResponse) return data.code
        return super.getStatusCode(data)
    }

    override fun resolverValue(data: Any?): ByteArray {
        if (data is BeanHttpResponse && data.data!=null){ //如果有数据则进行json转换
            if (data.data::class.java.isBaseType() || data.data::class.java.isString()) return data.data.toString().toByteArray()
            return jsonConvert.toJson(data.data)!!.toByteArray()
        }
        //无数据
        return ByteArray(0)
    }

    override fun getContentType(data: Any?): ContentType {
        return ContentType.TEXT_PLAIN
    }
}