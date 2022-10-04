package com.hxl.miniapi.core

import com.hxl.miniapi.core.convert.GsonConvert
import com.hxl.miniapi.core.convert.HttpParameteLocalDateTimeTypeConverter
import com.hxl.miniapi.core.convert.HttpParameterDateTypeConverter
import com.hxl.miniapi.core.convert.HttpParameterLocalDateTypeConverter
import com.hxl.miniapi.core.io.FileResourceLoader
import com.hxl.miniapi.core.io.JarResourceLoader
import com.hxl.miniapi.core.resolver.request.*
import com.hxl.miniapi.core.resolver.response.InputStreamResulResolver
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.core.resolver.response.SimpleTypeResultResolver
import com.hxl.miniapi.core.resolver.response.JsonResultResolver
import com.hxl.miniapi.core.resolver.response.StringResultResolver
import com.hxl.miniapi.http.HttpIntercept
import com.hxl.miniapi.http.server.CoolMiniWebServerImpl
import com.hxl.miniapi.http.server.WebServer
import com.hxl.miniapi.utils.*
import org.objectweb.asm.ClassReader
import java.net.URL

class MiniContext : Context {
    companion object {
        const val URL_PROTOCOL_FILE = "file"
        const val URL_PROTOCOL_JAR = "jar"
        private var REST_CONTROLLER_CODE = "L${RestController::class.java.name.replace(".", "/")};"
        private var COMPONENT_CLASS = arrayOf(REST_CONTROLLER_CODE)
    }

    private val httpParameterTypeConverter: MutableList<HttpParameterTypeConverter<*>> = mutableListOf()

    /**
     * 请求映射
     */
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping = RequestMappingHandlerMapping(this)

    /**
     * 参数解析器
     */
    private val argumentResolvers:MutableList<ArgumentResolver> = mutableListOf()

    /**
     * 结果转换器
     */
    private val resultResolver :MutableList<ResultResolver> = mutableListOf()
    /**
    * @description: 拦截器
    * @date: 2022/10/3 下午8:44
    */

    private val httpIntercepts: MutableList<HttpIntercept> = mutableListOf()

    private var jsonConvert:JsonConvert =GsonConvert()

    init {
        addArgumentResolvers(
            ReferenceArgumentResolver(this),
            FilePartArgumentResolver(),
            PathVariableArgumentResolver(),
            RequestParamSimpleTypeArgumentResolver(this),
            RequestUriArgumentResolver()
        )

        addHttpParameterTypeConverter(
            HttpParameteLocalDateTimeTypeConverter(),
            HttpParameterDateTypeConverter(),
            HttpParameterLocalDateTypeConverter(),
            HttpParameterLocalDateTypeConverter())

        addResultResolvers(StringResultResolver(),
            InputStreamResulResolver(),
            SimpleTypeResultResolver(),
            JsonResultResolver(this.jsonConvert))

    }
    override fun getArgumentResolvers(): List<ArgumentResolver> {
            return this.argumentResolvers
    }

    override fun getRequestMappingHandlerMapping(): RequestMappingHandlerMapping {
        return this.requestMappingHandlerMapping
    }

    override fun addHttpIntercept(httpIntercept: HttpIntercept) {
        this.httpIntercepts.add(httpIntercept)
    }

    /**
     * 所有component类
     */
    private val componentClass: MutableList<Class<*>> = mutableListOf()

    /**
     * 所有bean实例
     */
    private val beans  = mutableMapOf<Class<*>,Any>()

    override fun createWebServer(): WebServer {
        return CoolMiniWebServerImpl(this)
    }

    override fun refresh(start: Class<*>) {
        componentClass.clear()
        val resources = ClassLoader.getSystemClassLoader().getResources(start.`package`.name.replace(".", "/"))
        val classResources = mutableListOf<URL>()
        for (resource in resources) {
            if (URL_PROTOCOL_FILE == resource.protocol) {
                classResources.addAll(FileResourceLoader().getResources(resource.file))
            }
            if (URL_PROTOCOL_JAR == resource.protocol) {
                classResources.addAll(JarResourceLoader().getResources(resource.file))
            }
        }
        findComponentClass(classResources)
        registerIfRequestMapping()
    }

    /**
     * @description: 封装MappingInfo
     * @date: 2022/10/1 下午12:57
     */

    private fun registerIfRequestMapping() {
        this.componentClass.forEach{ clazz ->
            if (isRestControllerClass(clazz)) {
                for (method in clazz.declaredMethods) {
                    method.getRequestMappingInfo()?.run {
                        this.method=method
                        this.urlPatterns=method.getRequestMappingAnnotation().getDefaultValue().addPrefixIfMiss("/")
                        this.instance =beans.getOrPut(clazz){clazz.instance()}
                        requestMappingHandlerMapping.registerMapping(this)
                    }
                }
            }
        }
    }

    /**
     * @description: 是否是RestController类
     * @date: 2022/10/1 下午1:04
     */

     fun isRestControllerClass(clazz: Class<*>): Boolean {
        return clazz.getDeclaredAnnotation(RestController::class.java) != null
    }

    /**
     * @description: 找到所有component类
     * @date: 2022/10/1 下午12:56
     */

    private fun findComponentClass(classResources: List<URL>) {
        for (classResource in classResources) {
            val classNode = org.objectweb.asm.tree.ClassNode()
            ClassReader(classResource.openConnection().getInputStream()).accept(classNode, ClassReader.EXPAND_FRAMES)
            classNode.visibleAnnotations.forEach {
                if (COMPONENT_CLASS.contains(it.desc)) {
                    componentClass.add(classNode.name.toClass())
                }
            }
        }
    }

    override fun addArgumentResolvers(vararg resolver: ArgumentResolver) {
        resolver.forEach { this.argumentResolvers.add(it) }
    }

    override fun addResultResolvers(vararg resultResolver: ResultResolver) {
        resultResolver.forEach { this.resultResolver.add(it) }
    }

    override fun getResultResolvers(): List<ResultResolver> {
        return this.resultResolver
    }

    override fun setJsonConvert(jsonConvert: JsonConvert) {
        this.jsonConvert =jsonConvert
    }

    override fun getJsonConvert(): JsonConvert {
        return this.jsonConvert
    }

    override fun addHttpParameterTypeConverter(vararg httpParameterTypeConverter: HttpParameterTypeConverter<*>) {
        httpParameterTypeConverter.forEach { this.httpParameterTypeConverter.add(it) }
    }

    override fun getHttpParameterTypeConverter(): List<HttpParameterTypeConverter<*>> {
        return this.httpParameterTypeConverter
    }
}