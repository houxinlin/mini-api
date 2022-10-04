package com.hxl.miniapi.http

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.HandlerMapping
import com.hxl.miniapi.core.exception.ClientException
import com.hxl.miniapi.core.exception.HttpException
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler


class RequestDispatch(private val context: Context) : HttpHandler {
    //结果响应
    private val httpMessageResponse:HttpMessageResponse = HttpMessageResponse(context)
    override fun handle(http: HttpExchange) {
        try {
            val requestURI = http.requestURI

            val handler: HandlerMapping = context.getRequestMappingHandlerMapping().getHandler(requestURI.path, http)
                ?: throw   throw ClientException.create400("找不到处理器")
            //处理请求并返回结果
            val handlerResult = handler.handler(http)

            for (resultResolver in context.getResultResolvers()) {
                if (resultResolver.support(handlerResult)){
                    //向客户端返回结果
                   return resultResolver.resolver(handlerResult,http)
                }
            }

            httpMessageResponse.response(http,handlerResult)
        }catch (e:Exception){
            e.printStackTrace()
            handlerError(http,e)
        }
    }

    private fun handlerError(http: HttpExchange, e: Exception) {
        val message = e.message!!
       if (e is HttpException) {
           http.sendResponseHeaders(e.code, message.length.toLong())
       }else{
           http.sendResponseHeaders(500,message.length.toLong())
       }
        http.responseBody.write(message.toByteArray())
        http.responseBody.close()
    }
}