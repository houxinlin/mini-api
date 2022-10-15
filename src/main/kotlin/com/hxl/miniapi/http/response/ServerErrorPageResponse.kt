package com.hxl.miniapi.http.response

import com.hxl.miniapi.core.Context
import java.time.LocalDateTime

abstract class ServerErrorPageResponse : ErrorPageResponse {
    constructor():this(500)
    constructor(code:Int):super(code)
    override fun generatorTemplate(context: Context, exception: Exception): String {
        val result = mutableMapOf<String, Any>(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to 500,
            "error" to "Internal Server Error"
        )
        return  context.getJsonConvert()!!.toJson(result)!!
    }
}