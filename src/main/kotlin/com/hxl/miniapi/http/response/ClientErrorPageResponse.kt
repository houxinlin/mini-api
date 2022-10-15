package com.hxl.miniapi.http.response

import com.hxl.miniapi.core.Context
import java.lang.Exception

 abstract class ClientErrorPageResponse : ErrorPageResponse {
     constructor():this(400)
     constructor(code:Int):super(code)

     override fun generatorTemplate(context: Context, exception: Exception): String {
        return "Request Error"
    }
}