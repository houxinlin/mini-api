package com.hxl.miniapi.http

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.exception.HttpExceptionUtils
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.request.HttpRequestBase
import com.hxl.miniapi.http.response.HttpResponse
import com.hxl.miniapi.http.response.HttpResponseBase
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.io.ByteArrayOutputStream

/**
 * com.sun.net.httpserver请求入口
 */
class MiniWebHandler(private val context: Context) : HttpHandler {
    private val miniRequestDispatcher = MiniRequestDispatcher(context)
    override fun handle(http: HttpExchange) {
        //调用分发器
        val userResponse = ByteArrayOutputStream() //用户可能会响应输出
        val httpResponse = warpHttpResponse(http, userResponse)
        try {
            val result = miniRequestDispatcher.dispatcher(warpHttpRequest(http), httpResponse)
            //如果用户做了输出，则优先使用他
            val finalData = if (userResponse.size() != 0) userResponse else result.data
            if (userResponse.size() != 0 && httpResponse.getContentType() == null) {
                httpResponse.setContentType(ContentType.TEXT_PLAIN.contentType)
            }
            handlerResponse(http, finalData, httpResponse)
        } catch (e: Exception) {
            try {
                handlerResponse(http, e, httpResponse)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } finally {
            userResponse.close()
        }
    }

    /**
     * 处理响应，data数据可以为空
     */
    private fun handlerResponse(http: HttpExchange, data: Any?, httpResponse: HttpResponse) {
        for (resultResolver in context.getResultResolvers()) {
            if (resultResolver.support(data)) {
                //向客户端返回结果
                return resultResolver.resolver(data, http, httpResponse)
            }
        }
        throw HttpExceptionUtils.create500("结果无法正确响应")
    }

    /**
     * 包装一个http request对象
     */
    private fun warpHttpRequest(http: HttpExchange): HttpRequest {
        return HttpRequestBase(http).apply { this.setContext(context) }
    }

    private fun warpHttpResponse(http: HttpExchange, userResponse: ByteArrayOutputStream): HttpResponse {
        return HttpResponseBase(http, userResponse)
    }
}