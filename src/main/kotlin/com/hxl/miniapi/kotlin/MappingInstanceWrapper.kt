package com.hxl.miniapi.kotlin

import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.core.MiniContext
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse

class MappingInstanceWrapper(private val mini: MiniContext,private val kotlinRequestApi: KotlinRequestApi.() -> Unit) {
    fun handler(httpRequest: HttpRequest,httpResponse: HttpResponse) {
        val requestApi = KotlinRequestApi(mini,httpRequest,httpResponse)
        kotlinRequestApi.invoke(requestApi)
    }
}