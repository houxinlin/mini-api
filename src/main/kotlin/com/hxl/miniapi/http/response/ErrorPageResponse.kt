package com.hxl.miniapi.http.response

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.http.BaseResponse

abstract class ErrorPageResponse(code: Int) : BaseResponse(code) {
    abstract fun generatorTemplate(context:Context, exception: Exception): String
}