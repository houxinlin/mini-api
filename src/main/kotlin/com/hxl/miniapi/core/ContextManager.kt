package com.hxl.miniapi.core

import com.hxl.miniapi.http.session.Session
import com.hxl.miniapi.http.session.MiniSession
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

class ContextManager : Manager {
    private val sessionMap = ConcurrentHashMap<String, Session>()
    override fun findSession(sessionId: String): Session? {
        return sessionMap[sessionId]
    }

    override fun createNewSession(): Session {
        val sessionId = createSessionId()
        return putSession(sessionId, MiniSession(sessionId))
    }

    init {
        thread {
            //删除过期的key
            val cacheSession = mutableListOf<Session>()
            while (true) {
                for (value in sessionMap.values) {
                    if (value.getTnvalidTime() <System.currentTimeMillis()){
                        cacheSession.add(value)
                    }
                }
//                val sessionList = sessionMap.values.stream().filter { it.getTnvalidTime() < System.currentTimeMillis()  }
                cacheSession.forEach { removeSession(it.getSessionId()) }
                cacheSession.clear()
            }
        }
    }

    override fun removeSession(sessionId: String) {
        sessionMap.remove(sessionId)
    }

    /**
     * @description: 创建sessionId
     * @date: 2022/10/5 下午5:08
     */

    private fun createSessionId(): String {
        return UUID.randomUUID().toString()
    }


    /**
     * @description: 新增session
     * @date: 2022/10/5 下午5:08
     */

    private fun putSession(sessionId: String, session: Session): Session {
        sessionMap[sessionId] = session
        return session
    }

}