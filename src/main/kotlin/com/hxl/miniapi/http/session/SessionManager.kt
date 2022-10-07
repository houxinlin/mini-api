package com.hxl.miniapi.http.session

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object SessionManager {


    private val sessionMap = ConcurrentHashMap<String, Session>()

    init {
        thread {
            //删除过期的key
            while (true) {
                val sessionList = sessionMap.values.filter { it.getTnvalidTime() < System.currentTimeMillis() }
                sessionList.forEach {
                    removeSession(it.getSessionId())
                }
            }
        }
    }
     fun removeSession(sessionId:String){
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

    /**
     * @description: 获取session，如果不存在则创建新的session
     * @date: 2022/10/5 下午5:07
     */

    fun getSession(sessionId: String): Session? {
        if (sessionMap.containsKey(sessionId)) return sessionMap[sessionId]!!
        return null
    }


    /**
     * @description: 创建新的session
     * @date: 2022/10/5 下午5:07
     */

    fun newSession(): Session {
        val sessionId = createSessionId()
        return putSession(sessionId, SessionImpl(sessionId))
    }
}