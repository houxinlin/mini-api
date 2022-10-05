package com.hxl.miniapi.http

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.HandlerMapping
import com.hxl.miniapi.core.exception.ClientException
import com.hxl.miniapi.core.exception.HttpException
import com.hxl.miniapi.core.exception.ServerException
import com.hxl.miniapi.utils.startWhithPlus
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler


class RequestDispatch(private val context: Context) : HttpHandler {
    override fun handle(http: HttpExchange) {
        try {
            val requestAdapter = if (isFormDataRequest(http)) HttpMultipartAdapter(http) else HttpRequestAdapter(http)
            requestAdapter.getSession()//初始化session
            //1.default
            if (context.getAuthorization() == null) return doHandler(requestAdapter, http)

            //2.有拦截器，并且是认证url，放行
            if (requestAdapter.getRequestPath() == context.getAuthorization()!!.authUrl) {
                return doHandler(requestAdapter, http)//调用用户的登录接口
            }
            //3有拦截器，不是认证url，进行是否拦截
            val intercept = context.getAuthorization()!!.authentication.intercept(requestAdapter)//调用用户接口是否拦截
            //false则进行拦截
            if (intercept) {
                //交给用户处理
                context.getAuthorization()!!.authentication.postHandler(requestAdapter)
                requestAdapter.getResponse()?.run { setHttpHeader(http, this.header) }
                if (requestAdapter.getResponse() != null) {
                    return handlerResponse(requestAdapter.getResponse()!!.data, http)
                }
                return handlerResponse(NothingResponse(), http)
            }
            //true则不拦截
            doHandler(requestAdapter, http)
        } catch (e: Exception) {
            e.printStackTrace()
            handlerError(http, e)
        }
    }

    private fun setHttpHeader(httpExchange: HttpExchange, header: Map<String, String>) {
        header.forEach { (key, value) -> httpExchange.responseHeaders.add(key, value) }
    }

    private fun doHandler(requestAdapter: HttpRequestAdapter, http: HttpExchange) {
        val handler: HandlerMapping = context.getRequestMappingHandlerMapping().getHandler(requestAdapter)
            ?: throw throw ClientException.create400("找不到处理器")
        //结果处理器
        val handlerResult = handler.handler(requestAdapter)
        //处理
        requestAdapter.getResponse()?.run { setHttpHeader(http, this.header) }
        //如果用户用过setResponse方法设置响应，测优先返回此数据
        if (requestAdapter.getResponse() != null) {
            return handlerResponse(requestAdapter.getResponse()!!.data, http)
        }
        handlerResponse(handlerResult, http)
    }

    private fun handlerResponse(result: Any, http: HttpExchange) {
        for (resultResolver in context.getResultResolvers()) {
            if (resultResolver.support(result)) {
                //向客户端返回结果
                return resultResolver.resolver(result, http)
            }
        }
        throw ServerException.create500("无法转换结果")
    }

    private fun isFormDataRequest(httpExchange: HttpExchange): Boolean {
        val result = httpExchange.requestHeaders["Content-Type"]
            ?.find { it.startWhithPlus(ContentType.FORM_DATA.contentType) }
        return result != null
    }

    private fun handlerError(http: HttpExchange, e: Exception) {
        val message = e.message!!
        if (e is HttpException) {
            http.sendResponseHeaders(e.code, message.length.toLong())
        } else {
            http.sendResponseHeaders(500, message.length.toLong())
        }
        http.responseBody.write(message.toByteArray())
        http.responseBody.close()
    }
}