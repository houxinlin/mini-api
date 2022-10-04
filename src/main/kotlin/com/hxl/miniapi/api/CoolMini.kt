package com.hxl.miniapi.api

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.MiniContext
import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.ResultResolver

/**
 * @description: 应用程序编程入口
 * @date: 2022/10/1 上午10:25
 */

class CoolMini(val port: Int) {
    private val context: Context = MiniContext()
    fun start(start:Class<*>) {
        context.refresh(start)
        context.createWebServer().apply {
            this.init(port = this@CoolMini.port )
        }.start()
    }

    /**
     * @description: 增加参数转换器
     * @date: 2022/9/30 下午11:15
     */

    fun addArgumentResolvers(vararg argumentResolvers: ArgumentResolver) {
        context.addArgumentResolvers(*argumentResolvers)
    }

    /**
     * @description: 增加结果转换器
     * @date: 2022/9/30 下午11:16
     */

    fun addResultResolvers(vararg resolver: ResultResolver) {
        context.addResultResolvers(*resolver)
    }
}