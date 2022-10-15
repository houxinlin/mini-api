package com.hxl.miniapi.http

enum class ContentType(val contentType:String) {
    APPLICATION_JSON("application/json"),
    WWW_FORM_URLENCODEED("application/x-www-form-urlencoded"),
    TEXT_PLAIN("text/plain"),
    FORM_DATA("multipart/form-data"),
    APPLICATION_STREAM("application/octet-stream");

    fun applyCharset(charset:String): String {
        return "${this.contentType};charset=$charset"
    }

}