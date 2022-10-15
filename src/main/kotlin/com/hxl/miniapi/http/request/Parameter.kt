package com.hxl.miniapi.http.request

import com.hxl.miniapi.http.ContentType
import com.hxl.miniapi.http.MultipartParser
import com.hxl.miniapi.http.file.FilePart
import com.hxl.miniapi.utils.urlArgumentToMap


/**
 * @description: HTTPv参数解析
 * @date: 2022/10/14 上午1:39
 */

class Parameter(private val httpRequest: HttpRequest) {
    //string参数集合
    private val paramterMap: MutableMap<String, MutableList<String>> = mutableMapOf()

    private val fileParamterMap: MutableMap<String, MutableList<FilePart>> = mutableMapOf()

    init {
        parse()
    }

    /**
     * 参数解析
     */
    private fun parse() {
        //1.解析url参数
        val query = httpRequest.getQueryString()
        query?.urlArgumentToMap()?.forEach { (key, value) -> addParamter(key, value) }
        //2.解析application/x-www-form-urlencoded，如果不是此类型则跳过
        val contentType = httpRequest.getContentType()
        if (contentType.lowercase().startsWith(ContentType.WWW_FORM_URLENCODEED.contentType)) {
            val argumentMap = httpRequest.getBodyInputStream().readBytes().decodeToString().urlArgumentToMap()
            argumentMap.forEach { (key, value) -> addParamter(key, value) }
        }
        //3.解析form表单
        if (contentType.lowercase().startsWith( ContentType.FORM_DATA.contentType)) {
            getBoundary()?.run {
                val multipartParser = MultipartParser(httpRequest.getBodyInputStream().readBytes(), this)
                multipartParser.getPropertys().forEach { (key, value) ->
                    value.forEach { addParamter(key, it) }
                }
                multipartParser.getFiles().forEach { (key, value) ->
                    value.forEach {
                        if (!fileParamterMap.containsKey(key)) fileParamterMap[key] = mutableListOf()
                        fileParamterMap[key]!!.add(it)
                    }
                }
            }
        }
    }

    fun getFile(name: String): FilePart? {
        if (!fileParamterMap.containsKey(name)) return null
        if (fileParamterMap.isNotEmpty()) return fileParamterMap[name]!![0]
        return null
    }

    fun listFile(): MutableList<FilePart> {
        val result = mutableListOf<FilePart>()
        for (value in fileParamterMap.values) value.forEach(result::add)
        return result
    }

    fun getParamterKeys(): Set<String> {
        return paramterMap.keys
    }

    private fun getBoundary(): String? {
        val contentTypeValue = httpRequest.getContentType()
        if ( contentTypeValue.indexOf("boundary=") == -1) return null
        return "--${contentTypeValue.substring(contentTypeValue.indexOf("boundary=") + 9)}"
    }

    private fun addParamter(key: String, value: String) {
        if (!paramterMap.containsKey(key)) paramterMap[key] = mutableListOf()
        paramterMap[key]!!.add(value)
    }

    fun getParamter(key: String): String? {
        //从url参数中获取
        if (!paramterMap.containsKey(key)) return null
        val list = paramterMap[key]
        if (list!!.size == 1) return list[0]
        return list.toString()
    }
}