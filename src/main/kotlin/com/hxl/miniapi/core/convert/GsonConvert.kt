package com.hxl.miniapi.core.convert

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hxl.miniapi.core.JsonConvert
import java.lang.reflect.Type

class GsonConvert :JsonConvert{
    private val gson:Gson=Gson()
    override fun toJson(data: Any): String? {
        return gson.toJson(data)!!
    }

    override fun <T> fromJson(json: String, toClass: Class<T>): T? {
        return gson.fromJson(json,toClass)
    }

    override fun <T> fromJsonList(json: String, toClass: Class<T>): List<T>? {
        val type: Type = object : TypeToken<List<T>?>() {}.type
        return gson.fromJson(json,type)
    }
}