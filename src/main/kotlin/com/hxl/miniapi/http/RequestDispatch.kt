package com.hxl.miniapi.http

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.HandlerMapping
import com.hxl.miniapi.core.exception.ClientException
import com.hxl.miniapi.core.exception.HttpException
import com.hxl.miniapi.core.exception.ServerException
import com.hxl.miniapi.utils.startWhithPlus
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.time.LocalDateTime
import java.util.logging.Logger


class RequestDispatch(private val context: Context) : HttpHandler {
    private val logger: Logger = Logger.getLogger(RequestDispatch::class.java.name)
    override fun handle(http: HttpExchange) {
        try {
            val requestAdapter = if (isFormDataRequest(http)) HttpMultipartAdapter(http) else HttpRequestAdapter(http)
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
                return handlerResponse(InterceptResponse(), http)
            }
            //true则不拦截
            doHandler(requestAdapter, http)
        } catch (e: Exception) {
            if (e !is HttpException) e.printStackTrace()
            handlerError(http, e)
        }
    }

    private fun setHttpHeader(httpExchange: HttpExchange, header: Map<String, String>) {
        header.forEach { (key, value) -> httpExchange.responseHeaders.add(key, value) }
    }


    private fun doHandler(requestAdapter: HttpRequestAdapter, http: HttpExchange) {
        val handler: HandlerMapping = context.getRequestMappingHandlerMapping().getHandler(requestAdapter)
            ?: throw throw ClientException.create404("请求${requestAdapter.getRequestMethod()} ${requestAdapter.getRequestPath()}找不到映射")
        val handlerResult = handler.handler(requestAdapter)
        requestAdapter.getResponse()?.run { setHttpHeader(http, this.header) }
        //如果用户用过setResponse方法设置响应，则优先返回此数据
        //此处是拦截器产生
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
        throw ServerException.create500("结果无法正确响应")
    }

    private fun isFormDataRequest(httpExchange: HttpExchange): Boolean {
        val result = httpExchange.requestHeaders["Content-Type"]
            ?.find { it.startWhithPlus(ContentType.FORM_DATA.contentType) }
        return result != null
    }

    private fun handlerError(http: HttpExchange, e: Exception) {
        if (e !is HttpException)  return handlerServerError(http,e)
        //如果是能处理的异常，
        logger.warning(e.message)
        http.responseHeaders.set("Content-Type",ContentType.TEXT_PLAIN.contentType)
        http.sendResponseHeaders(e.code,0)
        http.responseBody.write(ByteArray(0))
        http.responseBody.close()
    }

    private fun handlerServerError(http: HttpExchange, e: Exception) {
        val result = mutableMapOf<String, Any>(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to 500,
            "error" to "Internal Server Error"
        )
        http.responseHeaders.set("Content-Type",ContentType.APPLICATION_JSON.contentType)
        val json :String= context.getJsonConvert().toJson(result)!!
        http.sendResponseHeaders(500,json.length.toLong())
        http.responseBody.write(json.toByteArray())
        http.responseBody.close()
    }
}