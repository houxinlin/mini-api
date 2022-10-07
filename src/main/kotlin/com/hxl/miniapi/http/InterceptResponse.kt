package com.hxl.miniapi.http

/**
* @description: 拦截响应状态码为403
* @date: 2022/10/5 上午11:01
*/

class InterceptResponse:BaseHttpResponse(HttpStatus.FORBIDDEN.code) {
}