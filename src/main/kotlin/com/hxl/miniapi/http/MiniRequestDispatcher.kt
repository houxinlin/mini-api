package com.hxl.miniapi.http

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.HandlerMapping
import com.hxl.miniapi.core.exception.HttpExceptionUtils
import com.hxl.miniapi.http.model.Model
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse
import com.hxl.miniapi.utils.AntPathMatcher
import java.util.logging.Logger

/**
 * 请求分发器
 */
class MiniRequestDispatcher(private val context: Context)  {
    private val logger: Logger = Logger.getLogger(MiniRequestDispatcher::class.java.name)
     fun dispatcher(httpRequest: HttpRequest,httpResponse:HttpResponse):Model {

         val antPathMatcher = AntPathMatcher()
         val registration = context.getHttpIntercept().find { intercept ->
             //如果不需要拦截 返回false
             if (intercept.getIncludePatterns().find { antPathMatcher.match(it, httpRequest.getRequestPath()) } == null) return@find false
             //如果需要拦截，但已经排除
             if (intercept.getExcludePatterns().find { antPathMatcher.match(it, httpRequest.getRequestPath()) } != null) return@find false
             return@find true
         }
         //如果有拦截器需要拦截,且拦截返回true
         if (registration!=null && registration.intercept.intercept(httpRequest, httpResponse)){
             registration.intercept.postHandler(httpRequest,httpResponse)
             return Model()
         }
         return  doDispatcher(httpRequest,httpResponse)
    }

    private fun doDispatcher(httpRequest: HttpRequest,httpResponse: HttpResponse):Model {
        //查找用户的映射方法
        val handler: HandlerMapping = context.getRequestMappingHandlerMapping().getHandler(httpRequest)
            ?: throw throw HttpExceptionUtils.create404("请求${httpRequest.getHttpMethod()} ${httpRequest.getUrl()}找不到映射")
        return handler.handler(httpRequest,httpResponse)
    }
}