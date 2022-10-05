package com.hxl.miniapi.http.session


/**
* @description: 会话
* @date: 2022/10/5 上午6:40
*/

interface Session {
    fun getAttibute(key:String,default:Any?):Any?

    fun setAttribute(key: String,value:Any)

    fun clearAttribute()

    fun getSessionId():String


}