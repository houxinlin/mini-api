package com.hxl.miniapi.http.session

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class MiniSession(private val sId: String) : Session {
    private val sessionAttributeMap = ConcurrentHashMap<String, Any?>()
    private var maxInactiveInterval: Long = System.currentTimeMillis()+TimeUnit.HOURS.toMicros(1)
    override fun getAttibute(key: String, default: Any?): Any? {
        return sessionAttributeMap.getOrDefault(key, default)
    }

    override fun setTnvalidTime(value: Long) {
        this.maxInactiveInterval = System.currentTimeMillis() + value
    }

    override fun removeAttribute(key: String) {
        sessionAttributeMap.remove(key)
    }

    override fun getAttributeKeys(): Set<String> {
        return sessionAttributeMap.keys.toSet()
    }

    override fun getTnvalidTime(): Long {
        return this.maxInactiveInterval
    }

    override fun setAttribute(key: String, value: Any) {
        sessionAttributeMap[key] = value
    }

    override fun clearAttribute() {
        sessionAttributeMap.clear()
    }

    override fun getSessionId(): String {
        return sId
    }
}