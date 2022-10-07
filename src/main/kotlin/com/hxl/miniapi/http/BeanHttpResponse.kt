package com.hxl.miniapi.http

/**
* @description: 带有数据和状态码的响应
* @date: 2022/10/5 上午11:01
*/

class BeanHttpResponse( code:Int,val data:Any?):BaseHttpResponse(code) {
}