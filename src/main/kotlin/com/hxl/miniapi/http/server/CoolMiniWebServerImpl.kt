package com.hxl.miniapi.http.server

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.http.MiniWebHandler
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class CoolMiniWebServerImpl(private val context: Context):WebServer {
    private val logger =LoggerFactory.getLogger(WebServer::class.java.simpleName)
    private val webServer: HttpServer = HttpServer.create()
    private var miniWebHandler = MiniWebHandler(context)

    private fun createThreadPoolExecutor(): ThreadPoolExecutor {
        val processors = Runtime.getRuntime().availableProcessors()
        return ThreadPoolExecutor(processors,processors,1,TimeUnit.SECONDS,LinkedBlockingQueue())
    }

    private var port =0
    override fun start() {
        webServer.bind(InetSocketAddress(port), BACKLOG)
        webServer.createContext("/",miniWebHandler)
        webServer.executor =createThreadPoolExecutor()
        webServer.start()
        logger.info("服务启动于:[$port]")
    }

    override fun stop() {
        webServer.stop(1)
    }

    override fun init(port: Int) {
        this.port=port
    }

    companion object{
        const val BACKLOG:Int =50
    }
}