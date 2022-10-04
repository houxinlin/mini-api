package com.hxl.miniapi.http.server

import com.hxl.miniapi.core.Context

interface WebServer {

    /**
    * @description: 启动web服务
    * @date: 2022/10/1 下午2:19
    */

    fun start()

    /**
    * @description: 停止web服务
    * @date: 2022/10/1 下午2:19
    */

    fun stop()

    /**
    * @description: 初始化
    * @date: 2022/10/1 下午2:19
    */

    fun init(port:Int)
}