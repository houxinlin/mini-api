package com.hxl.miniapi.core


/**
* @description: json转换器
* @date: 2022/10/3 下午9:10
*/

interface JsonConvert {
    fun toJson(data:Any):String?

    fun <T> fromJson(json: String ,toClass: Class<T>):T?

    fun <T> fromJsonList(json: String, toClass: Class<T>):List<T>?
}