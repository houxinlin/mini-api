package com.hxl.miniapi.http

enum class HttpStatus(val code:Int) {
    SUCCESS(200),
    CLIENT_ERROR(401),
    NOT_FOUND(404)
}