package com.hxl.miniapi.core.resolver.response

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.ResultResolver
import com.hxl.miniapi.core.exception.ClientException
import com.hxl.miniapi.core.exception.HttpException
import com.hxl.miniapi.core.exception.ServerException
import com.hxl.miniapi.http.ContentType
import org.apache.ibatis.logging.LogFactory
import java.time.LocalDateTime

/**
 * 处理异常返回
 */
class ExceptionResponseResolver(private val context: Context) : ResultResolver() {
    private val log = LogFactory.getLog(ExceptionResponseResolver::class.java)
    override fun support(data: Any?): Boolean {
        return data != null && data is Exception
    }

    override fun getStatusCode(data: Any?): Int {
        if (data is HttpException) return data.code
        return 500
    }

    override fun resolverValue(data: Any?): ByteArray {
        if (data is ClientException) {
            log.error(data.message)
            return context.getClientErrorPageResponse().generatorTemplate(context, data).toByteArray()
        }
        if (data is ServerException) {
            log.error(data.message)
            return context.getServerErrorPageResponse().generatorTemplate(context, data).toByteArray()
        }
        if (data is Exception) {
            data.printStackTrace()
        }
        return """ ${LocalDateTime.now()} 
There was an unexpected error (type=Internal Server Error, status=500). """.trimMargin().toByteArray()
    }

    override fun getContentType(data: Any?): ContentType {
        return ContentType.TEXT_PLAIN
    }
}