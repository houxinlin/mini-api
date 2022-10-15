package com.hxl.miniapi.http

import com.hxl.miniapi.core.exception.HttpExceptionUtils
import com.hxl.miniapi.http.file.FilePart
import com.hxl.miniapi.utils.startWhithPlus
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @description: multi解析
 */

class MultipartParser(private val requestBody: ByteArray, private val boundary: String) {
    private val parameterMap: MutableMap<String, MutableList<String>> = mutableMapOf()
    private val fileParameterMap: MutableMap<String, MutableList<FilePart>> = mutableMapOf()
    init {
        parser(requestBody)
    }
    companion object {
        const val FILENAME_KEY = "filename"
        const val NAME_KEY = "name"
        const val CONTENT_TYPE = "Content-Type"
        const val CONTENT_DISPOSITION = "Content-Disposition"
    }

    /**
     * 获取文件
     */
    fun getFiles(): MutableMap<String, MutableList<FilePart>> {
        return fileParameterMap
    }

    /**
     * 获取属性
     */
    fun getPropertys(): MutableMap<String, MutableList<String>>{
        return parameterMap
    }

    private fun parser(bodyByte: ByteArray): MutableMap<String, MutableList<Any>> {
        if (bodyByte.isEmpty()) throw HttpExceptionUtils.create400("客户端请求错误")
        val lineInputStream = LineInputStream(bodyByte, boundary)
        val result = mutableMapOf<String, MutableList<Any>>()
        while (lineInputStream.readLine()?.also {
                if (it.decodeToString() != boundary) {
                    //如果是结束标志
                    if (it.decodeToString() == "${boundary}--") return@also
                    //不是结束标志也不是开始标志
                    throw HttpExceptionUtils.create400("客户端请求错误")
                }

                val property = mutableMapOf<String, String>()
                while (!lineInputStream.nextIs("\r\n")) {
                    property.putAll(findDisposition(lineInputStream.readLine()!!.decodeToString()))
                }
                //跳过属性
                lineInputStream.skip(2)

                val keyValue = lineInputStream.readValue()
                val nameKey = property[NAME_KEY]!!
                //如果是文件
                if (property.containsKey(FILENAME_KEY)) {
                    if (!fileParameterMap.containsKey(nameKey)) fileParameterMap[nameKey] = mutableListOf()
                    fileParameterMap[nameKey]!!.add(FilePart().apply {
                        this.name = property[NAME_KEY]!!
                        this.contentType = property[CONTENT_TYPE]!!
                        this.inputStream = ByteArrayInputStream(keyValue)
                        this.contentLength = keyValue.size.toLong()
                    })
                } else {
                    if (!parameterMap.containsKey(nameKey)) parameterMap[nameKey] = mutableListOf()
                    parameterMap[nameKey]!!.add(keyValue.decodeToString())
                }

            } != null) {
        }
        return result
    }

    /**
     * @description: 解析描述字符
     * @date: 2022/10/4 下午7:54
     */

    private fun findDisposition(source: String): MutableMap<String, String> {
        val nameRegex: Matcher = Pattern.compile("[^file]name=\"(.*?)\"").matcher(source)
        val fileNameRegex = Pattern.compile("filename=\"(.*)\"").matcher(source)

        val result = mutableMapOf<String, String>()
        if (source.startWhithPlus("$CONTENT_DISPOSITION:") && nameRegex.find()) {
            result[NAME_KEY] = nameRegex.group(1)
        }
        if (source.startWhithPlus("$CONTENT_DISPOSITION:") && fileNameRegex.find()) {
            result[FILENAME_KEY] = fileNameRegex.group(1)
        }
        if (source.startWhithPlus(CONTENT_TYPE)) {
            result[CONTENT_TYPE] = source.substring("$CONTENT_TYPE:".length).trim()
        }
        return result
    }

    class LineInputStream(private val bodyByte: ByteArray, private val boundary: String) {
        private var offset: Int = 0
        fun readLine(): ByteArray? {
            val arrayOutputStream = ByteArrayOutputStream()
            while (offset < bodyByte.size && bodyByte[offset++] != '\r'.code.toByte()) {
                arrayOutputStream.write(bodyByte[offset - 1].toInt())
            }
            moveToNext()
            if (arrayOutputStream.toByteArray().isEmpty()) return null
            return arrayOutputStream.toByteArray()
        }

        fun readValue(): ByteArray {
            val arrayOutputStream = ByteArrayOutputStream()
            nextPartPostion().run {
                for (i in offset until this - 2) {
                    arrayOutputStream.write(bodyByte[i].toInt())
                }
                offset = this
            }
            return arrayOutputStream.toByteArray()
        }

        fun nextIs(target: String): Boolean {
            val arrayOutputStream = ByteArrayOutputStream()
            for (i in offset until offset + target.length) {
                arrayOutputStream.write(bodyByte[i].toInt())
            }
            return arrayOutputStream.toByteArray().decodeToString() == target
        }

        private fun nextPartPostion(): Int {
            var tempOffset = this.offset
            val arrayOutputStream = ByteArrayOutputStream()
            while (arrayOutputStream.toByteArray().decodeToString() != boundary) {
                //如果当前位置+boundary.length还没超出范围
                arrayOutputStream.reset()
                if (tempOffset + boundary.length > bodyByte.size) throw HttpExceptionUtils.create400("客户端请求错误")
                //取这个阶段的范围
                for (i in tempOffset until tempOffset + boundary.length) {
                    arrayOutputStream.write(bodyByte[i].toInt())
                }
                tempOffset++
            }
            //返回下一个item开始的位置，但包含\r\n需要去除
            return tempOffset - 1
        }

        private fun moveToNext() {
            while (offset < bodyByte.size && bodyByte[offset++] != '\n'.code.toByte()) {
            }
        }

        fun skip(i: Int) {
            offset += i
        }
    }
}