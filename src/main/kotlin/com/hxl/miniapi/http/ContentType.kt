package com.hxl.miniapi.http

enum class ContentType(val contentType:String) {
    APPLICATION_JSON("application/json; charset=utf-8"),
    WWW_FORM_URLENCODEED("application/x-www-form-urlencoded; charset=utf-8"),
    TEXT_PLAIN("text/plain; charset=utf-8"),
    FORM_DATA("multipart/form-data; charset=utf-8"),
    APPLICATION_STREAM("application/octet-stream; charset=utf-8")

}