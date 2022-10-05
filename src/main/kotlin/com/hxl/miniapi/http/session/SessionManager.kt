package com.hxl.miniapi.http.session

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object SessionManager {
    private val sessionMap =ConcurrentHashMap<String,Session>()

    /**
    * @description: 创建sessionId
    * @date: 2022/10/5 下午5:08
    */

    private fun createSessionId():String{
        return UUID.randomUUID().toString()
    }


    /**
    * @description: 新增session
    * @date: 2022/10/5 下午5:08
    */

    private fun putSession(sessionId:String, session: Session){
        sessionMap[sessionId] =session
    }

    /**
    * @description: 获取session，如果不存在则创建新的session
    * @date: 2022/10/5 下午5:07
    */

    fun getSession(sessionId: String):Session{
        return sessionMap.getOrPut(sessionId) { SessionImpl(sessionId) }
    }


    /**
    * @description: 创建新的session
    * @date: 2022/10/5 下午5:07
    */

    fun newSession():String {
        val sessionId = createSessionId()
        putSession(sessionId,SessionImpl(sessionId))
        return  sessionId
    }
}