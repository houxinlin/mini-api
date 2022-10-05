package com.hxl.miniapi.core

import com.hxl.miniapi.core.auth.MiniAuthentication
import com.hxl.miniapi.core.convert.GsonConvert
import com.hxl.miniapi.core.convert.HttpParameteLocalDateTimeTypeConverter
import com.hxl.miniapi.core.convert.HttpParameterDateTypeConverter
import com.hxl.miniapi.core.convert.HttpParameterLocalDateTypeConverter
import com.hxl.miniapi.core.io.FileResourceLoader
import com.hxl.miniapi.core.io.JarResourceLoader
import com.hxl.miniapi.core.resolver.request.*
import com.hxl.miniapi.core.resolver.response.*
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.HttpIntercept
import com.hxl.miniapi.http.server.CoolMiniWebServerImpl
import com.hxl.miniapi.http.server.WebServer
import com.hxl.miniapi.orm.AutowriteCrud
import com.hxl.miniapi.orm.BaseCrudRepository
import com.hxl.miniapi.orm.Mybatis
import com.hxl.miniapi.orm.MybatisCrudRepository
import com.hxl.miniapi.utils.*
import org.objectweb.asm.ClassReader
import java.lang.reflect.Method
import java.net.URL
import javax.sql.DataSource

open class MiniContext : Context {
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
    private val argumentResolvers: MutableList<ArgumentResolver> = mutableListOf()

    /**
     * 结果转换器
     */
    private val resultResolver: MutableList<ResultResolver> = mutableListOf()

    /**
     * @description: 拦截器
     */

    private val httpIntercepts: MutableList<HttpIntercept> = mutableListOf()


    /**
     * json转换器
     */
    private var jsonConvert: JsonConvert = GsonConvert()

    /**
     * 认证器
     */
    private var authentication: MiniAuthentication? = null

    private var dataSource: DataSource? = null

    init {
        addArgumentResolvers(
            SessionArgumentResolver(),
            FilePartArgumentResolver(),
            PathVariableArgumentResolver(),
            RequestParamSimpleTypeArgumentResolver(this),
            RequestUriArgumentResolver(),
            ReferenceArgumentResolver(this),
        )

        //用于将String到T类型
        addHttpParameterTypeConverter(
            HttpParameteLocalDateTimeTypeConverter(),
            HttpParameterDateTypeConverter(),
            HttpParameterLocalDateTypeConverter(),
            HttpParameterLocalDateTypeConverter()
        )

        addResultResolvers(
            NothingResponseResolver(),
            StringResultResolver(),
            InputStreamResulResolver(),
            SimpleTypeResultResolver(),
            JsonResultResolver(this.jsonConvert)
        )

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

    override fun getHttpIntercept(): List<HttpIntercept> {
        return this.httpIntercepts
    }

    /**
     * 所有component类
     */
    private val componentClass: MutableList<Class<*>> = mutableListOf()

    /**
     * 所有bean实例
     */
    private val beans = mutableMapOf<Class<*>, Any>()

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
        //如果存在数据源配置，则自动注入依赖
        this.beans.values.forEach(this::autowriteInjection)

    }


    /**
    * @description: 依赖注入
    * @date: 2022/10/6 上午6:36
    */

    private fun autowriteInjection(bean:Any) {
        if (dataSource==null) return
        val mybatis = MybatisCrudRepository(Mybatis(dataSource!!))

        val beanFields = bean::class.java.declaredFields
        beanFields.forEach {
            if (it.getDeclaredAnnotation(AutowriteCrud::class.java)!=null &&
                BaseCrudRepository::class.java.isAssignableFrom(it.type)){
                it.isAccessible=true
                it.set(bean,mybatis)
            }
        }

    }


    /**
     * @description: 封装MappingInfo
     * @date: 2022/10/1 下午12:57
     */

    private fun registerIfRequestMapping() {
        this.componentClass.forEach { clazz ->
            //如果是标有@RestController的类
            if (isRestControllerClass(clazz)) clazz.declaredMethods.forEach(this::extractMappingMethod)
        }
    }


    /**
    * @description: 提取并注册Mapping方法
    * @date: 2022/10/6 上午6:34
    */

    private fun extractMappingMethod(method: Method) {
        if (method.declaringClass != Any::class.java) {
            method.getRequestMappingInfo()?.run {
                this.method = method
                this.urlPatterns = method.getRequestMappingAnnotation().getDefaultValue().addPrefixIfMiss("/")
                this.instance = beans.getOrPut(method.declaringClass) { method.declaringClass.instance() }
                //注册mapping映射
                requestMappingHandlerMapping.registerMapping(this)
            }
        }
    }

    /**
     * @description: 是否是RestController类
     * @date: 2022/10/1 下午1:04
     */

    private fun isRestControllerClass(clazz: Class<*>): Boolean {
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

    override fun addArgumentResolvers(vararg argumentResolvers: ArgumentResolver) {
        argumentResolvers.forEach { this.argumentResolvers.add(it) }
    }

    override fun addResultResolvers(vararg resultResolver: ResultResolver) {
        resultResolver.forEach { this.resultResolver.add(it) }
    }

    override fun getResultResolvers(): List<ResultResolver> {
        return this.resultResolver
    }

    override fun setJsonConvert(jsonConvert: JsonConvert) {
        this.jsonConvert = jsonConvert
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

    override fun setAuthorization(authentication: MiniAuthentication) {
        if (authentication.authUrl.isEmpty()) throw IllegalArgumentException("url参数不能为空")
        val newUrl = if (authentication.authUrl == "/") "/" else {
            if (!authentication.authUrl.startsWith("/")) {
                "/${authentication.authUrl}"
            } else {
                authentication.authUrl
            }
        }
        this.authentication = MiniAuthentication(newUrl.removeSuffix("/"), authentication.authentication)
    }

    override fun setDataSource(dataSource: DataSource) {
        this.dataSource =dataSource
    }

    override fun getAuthorization(): MiniAuthentication? {
        return this.authentication
    }
}