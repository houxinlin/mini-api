package com.hxl.miniapi.http.server

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.http.RequestDispatch
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class CoolMiniWebServerImpl(private val context: Context):WebServer {
    private val webServer: HttpServer = HttpServer.create()
    private var requestDispatch = RequestDispatch(context)
    private var port =0
    override fun start() {
        webServer.bind(InetSocketAddress(port), BACKLOG)
        webServer.createContext("/",requestDispatch)
        webServer.start()
        println("启动")
    }

    override fun stop() {
    }

    override fun init(port: Int) {
        this.port=port
    }

    companion object{
        const val BACKLOG:Int =50
    }
}