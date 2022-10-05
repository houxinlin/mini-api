package com.hxl.miniapi.http.session

import java.util.concurrent.ConcurrentHashMap

class SessionImpl(private val sId:String):Session {
    private val sessionAttributeMap =ConcurrentHashMap<String,Any?>()
    override fun getAttibute(key: String, default: Any?): Any? {
        return sessionAttributeMap.getOrDefault(key,default)
    }

    override fun setAttribute(key: String, value: Any) {
        sessionAttributeMap[key]=value
    }

    override fun clearAttribute() {
        sessionAttributeMap.clear()
    }

    override fun getSessionId(): String {
        return sId
    }
}