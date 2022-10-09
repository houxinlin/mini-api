package com.hxl.miniapi.http.cookie

/**
* @description: cookie管理
* @date: 2022/10/10 上午3:50
*/


interface Cookie {
    /**
     * 遍历key
     */
    fun getKeys():Set<String>

    /**
     * 获取路径
     */
    fun getPath():String

    /**
     * 设置路径
     */
    fun setPath()

    /**
     * 设置SameSite
     */
    fun setSameSite(value:String)

    /**
     * 获取SameSite
     */
    fun getSameSite():String

    /**
     * 设置过期最大时间
     */
    fun setMaxAge(value:String)

    /**
     * 获取过期最大时间
     */
    fun getMaxAge(value: String)


    /**
     * 设置过期时间
     */
    fun setExpires(value: String)

    /**
     * 获取过期时间
     */
    fun getExpores():String

    /**
     * 获取属性
     */
    fun getAttribute(key:String):String

    /**
     * 设置属性
     */
    fun setAttribute(key: String,value: String)
}