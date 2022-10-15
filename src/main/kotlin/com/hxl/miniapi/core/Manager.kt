package com.hxl.miniapi.core

import com.hxl.miniapi.http.session.Session

interface Manager {
   companion object{
       const val SESSION_ID = "MINI_API_SESSION"
   }
    fun findSession(sessionId:String):Session?

    fun createNewSession():Session

    fun removeSession(sessionId: String)
}