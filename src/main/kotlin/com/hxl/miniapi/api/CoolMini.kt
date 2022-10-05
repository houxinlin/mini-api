package com.hxl.miniapi.api

import com.hxl.miniapi.core.MiniContext
/**
 * @description: 应用程序编程入口
 * @date: 2022/10/1 上午10:25
 */

class CoolMini(val port: Int) :MiniContext(){
    fun start(start:Class<*>) {
        refresh(start)
        createWebServer().apply {
            init(port = this@CoolMini.port )
        }.start()
    }
}