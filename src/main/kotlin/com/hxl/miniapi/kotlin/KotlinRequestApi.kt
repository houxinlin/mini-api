package com.hxl.miniapi.kotlin

import com.hxl.miniapi.core.MiniContext
import com.hxl.miniapi.http.file.FilePart
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse
import com.hxl.miniapi.orm.mybatis.IMybatisCrudRepository
import kotlin.math.min

class KotlinRequestApi(
    private val mini: MiniContext,
    val httpRequest: HttpRequest,
    val httpResponse: HttpResponse
) {
    fun setCode(code: Int) = httpResponse.setStatus(code)
    fun setResult(msg: String) = httpResponse.getOutputStream().write(msg.toByteArray())
    fun setResult(msg: ByteArray) = httpResponse.getOutputStream().write(msg)

    fun toJson(data: Any): String = mini.getJsonConvert()!!.toJson(data)

    fun getRequestParam(param: String): String? = httpRequest.getParameter(param)
    fun getRequestFile(name: String): FilePart? = httpRequest.getFile(name)

    fun getMybatisCrudRepository(): IMybatisCrudRepository? = mini.getMybatisRepositoryProxy()

    fun runSqlListMap(querySql: String, vararg arg: Any): List<Map<String, Any>> = getMybatisCrudRepository()!!.listMap(querySql, *arg)

    fun <T> runSqlList(querySql: String, ofClass: Class<T>, vararg arg: Any): List<T> = getMybatisCrudRepository()!!.list(querySql, ofClass, *arg)

    fun <T> runSqlListOne(querySql: String, ofClass: Class<T>,vararg arg:Any):T? = getMybatisCrudRepository()!!.listOne(querySql, ofClass, *arg)

    fun update(querySql: String,vararg arg:Any):Int = getMybatisCrudRepository()!!.update(querySql, *arg)
}