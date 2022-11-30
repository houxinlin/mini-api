package com.hxl.miniapi.core

import com.google.gson.Gson
import com.hxl.miniapi.core.convert.GsonConvert
import com.hxl.miniapi.core.convert.HttpParameteLocalDateTimeTypeConverter
import com.hxl.miniapi.core.convert.HttpParameterDateTypeConverter
import com.hxl.miniapi.core.convert.HttpParameterLocalDateTypeConverter
import com.hxl.miniapi.core.io.FileResourceLoader
import com.hxl.miniapi.core.io.JarResourceLoader
import com.hxl.miniapi.core.resolver.request.*
import com.hxl.miniapi.core.resolver.response.*
import com.hxl.miniapi.http.HttpIntercept
import com.hxl.miniapi.http.InterceptorRegistration
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.response.ClientErrorPageResponse
import com.hxl.miniapi.http.response.ServerErrorPageResponse
import com.hxl.miniapi.http.server.CoolMiniWebServerImpl
import com.hxl.miniapi.http.server.WebServer
import com.hxl.miniapi.kotlin.MiniKotlinApi
import com.hxl.miniapi.orm.*
import com.hxl.miniapi.orm.mybatis.IMybatisCrudRepository
import com.hxl.miniapi.orm.mybatis.Mybatis
import com.hxl.miniapi.orm.mybatis.MybatisAutoSessionProxy
import com.hxl.miniapi.orm.mybatis.MybatisCrudRepository
import com.hxl.miniapi.utils.*
import org.objectweb.asm.ClassReader
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy
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
     *  拦截器
     */

    private val httpIntercepts: MutableList<InterceptorRegistration> = mutableListOf()

    /**
     * gson
     */
    private var gson: Gson? = null

    /**
     * json转换器
     */
    private var jsonConvert: JsonConvert? = null

    /**
     * 数据源
     */
    private var dataSource: DataSource? = null

    /**
     * 所有component类
     */
    private val componentClass: MutableList<Class<*>> = mutableListOf()

    /**
     * 所有bean实例
     */
    private val beans = mutableMapOf<Class<*>, Any>()

    private var mybatisRepositoryProxy: IMybatisCrudRepository? = null
    private var mybatisRepositoryReal: IMybatisCrudRepository? = null

    private var clientErrorPageResponse: ClientErrorPageResponse = object : ClientErrorPageResponse() {}
    private var serverErrorPageResponse: ServerErrorPageResponse = object : ServerErrorPageResponse() {}
    private val manager = ContextManager()
    private val fileResourceLoader = FileResourceLoader()
    private val jarResourceLoader = JarResourceLoader()

    init {
        addArgumentResolvers(
            false,
            RequestRawParamResolver(),
            SessionArgumentResolver(),
            RequestUriArgumentResolver(),
            FilePartArgumentResolver(),
            PathVariableArgumentResolver(),
            RequestBodyArgumentResolver(this),
            RequestParamSimpleTypeArgumentResolver(this),
        )

        //用于将String到T类型
        addHttpParameterTypeConverter(
            false,
            HttpParameteLocalDateTimeTypeConverter(),
            HttpParameterDateTypeConverter(),
            HttpParameterLocalDateTypeConverter(),
            HttpParameterLocalDateTypeConverter()
        )

        addResultResolvers(
            false,
            NullResultResolver(),
            ExceptionResponseResolver(this),
            StringResultResolver(),
            ByteStreamResulResolver(),
            SimpleTypeResultResolver(),
            JsonResultResolver(this)
        )
    }

    fun getMybatisRepositoryProxy(): IMybatisCrudRepository? = this.mybatisRepositoryProxy

    override fun withKotlin(function: MiniKotlinApi.() -> Unit) {
        val miniKotlinApi = MiniKotlinApi(this)
        function.invoke(miniKotlinApi)
    }

    override fun refresh(start: Class<*>) {
        if (this.gson == null) this.gson = Gson()
        if (this.jsonConvert == null) this.jsonConvert = GsonConvert(this.gson!!)
        val packageInfo = start.`package`
        val name = if (packageInfo == null) "" else packageInfo.name
        val resources = ClassLoader.getSystemClassLoader().getResources(name.replace(".", "/"))
        val classResources = mutableListOf<URL>()
        for (resource in resources) {
            if (URL_PROTOCOL_FILE == resource.protocol) {
                classResources.addAll(fileResourceLoader.getResources(resource.file))
            }
            if (URL_PROTOCOL_JAR == resource.protocol) {
                classResources.addAll(jarResourceLoader.getResources(resource.file))
            }
        }
        //从类路径下找到所有component类，目前只有标有RestContrller的类才会被找到
        findComponentClass(classResources)
        //从标有@RestController的类下注册所有mapping
        registerIfRequestMapping()
        //创建Mybatis实例
        createMybatisRepository()
        //如果存在数据源配置，则自动注入依赖
        this.beans.values.forEach(this::autowriteInjection)
        //调用bean的初始化方法
        invokeBeanInitMethod()

    }

    override fun setGson(gson: Gson) {
        this.gson = gson
    }

    override fun getGson(): Gson? {
        return this.gson
    }

    override fun registerController(vararg controllerClass: Class<*>) {
        controllerClass.forEach(componentClass::add)
    }

    override fun getArgumentResolvers(): List<ArgumentResolver> {
        return this.argumentResolvers
    }

    override fun getRequestMappingHandlerMapping(): RequestMappingHandlerMapping {
        return this.requestMappingHandlerMapping
    }

    override fun addHttpIntercept(httpIntercept: HttpIntercept): InterceptorRegistration {
        val registration = InterceptorRegistration(httpIntercept)
        this.httpIntercepts.add(registration)
        return registration
    }

    override fun getHttpIntercept(): List<InterceptorRegistration> {
        return this.httpIntercepts
    }

    override fun createWebServer(): WebServer {
        return CoolMiniWebServerImpl(this)
    }

    /**
     * @description: 调用对象init方法
     * @date: 2022/10/7 上午10:01
     */

    private fun invokeBeanInitMethod() {
        for (bean in this.beans.values) {
            try {
                val initMethodHandle =
                    MethodHandles.lookup().findVirtual(bean::class.java, "init", MethodType.methodType(Void.TYPE))
                initMethodHandle.invoke(bean)
            } catch (e: Exception) {
            }
        }
    }

    private fun createMybatisRepository() {
        if (dataSource == null) return
        if (mybatisRepositoryProxy == null) {
            mybatisRepositoryReal = MybatisCrudRepository(Mybatis(dataSource!!))
            mybatisRepositoryProxy = Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(), IFACES,
                MybatisAutoSessionProxy(mybatisRepositoryReal!!)
            ) as IMybatisCrudRepository
        }
    }


    /**
     * @description: 依赖注入
     * @date: 2022/10/6 上午6:36
     */
    private val IFACES: Array<Class<*>> = arrayOf(IMybatisCrudRepository::class.java)
    private fun autowriteInjection(bean: Any) {
        if (mybatisRepositoryProxy == null) return
        val beanFields = bean::class.java.declaredFields
        beanFields.forEach {
            if (it.getDeclaredAnnotation(AutowriteCrud::class.java) != null &&
                !Modifier.isFinal(it.modifiers) &&
                CrudRepository::class.java.isAssignableFrom(it.type)
            ) {
                it.isAccessible = true
                it.set(bean, mybatisRepositoryProxy)
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
            if (classNode.visibleAnnotations == null) continue
            classNode.visibleAnnotations.forEach {
                if (COMPONENT_CLASS.contains(it.desc)) {
                    componentClass.add(classNode.name.toClass())
                }
            }
        }
    }

    override fun addArgumentResolvers(first: Boolean, vararg argumentResolvers: ArgumentResolver) {
        argumentResolvers.forEach { if (first) this.argumentResolvers.add(0, it) else this.argumentResolvers.add(it) }
    }

    override fun addResultResolvers(first: Boolean, vararg resultResolver: ResultResolver) {
        resultResolver.forEach { if (first) this.resultResolver.add(0, it) else this.resultResolver.add(it) }
    }

    override fun getResultResolvers(): List<ResultResolver> {
        return this.resultResolver
    }

    override fun setJsonConvert(jsonConvert: JsonConvert) {
        this.jsonConvert = jsonConvert
    }

    override fun getJsonConvert(): JsonConvert? {
        return this.jsonConvert
    }

    override fun addHttpParameterTypeConverter(
        first: Boolean,
        vararg httpParameterTypeConverter: HttpParameterTypeConverter<*>
    ) {
        httpParameterTypeConverter.forEach {
            if (first) this.httpParameterTypeConverter.add(
                0,
                it
            ) else this.httpParameterTypeConverter.add(it)
        }
    }

    override fun getHttpParameterTypeConverter(): List<HttpParameterTypeConverter<*>> {
        return this.httpParameterTypeConverter
    }

    override fun setDataSource(dataSource: DataSource) {
        this.dataSource = dataSource
    }

    override fun getManager(): Manager {
        return this.manager
    }

    override fun setClientErrorPageResponse(clientErrorPageResponse: ClientErrorPageResponse) {
        this.clientErrorPageResponse = clientErrorPageResponse
    }

    override fun getClientErrorPageResponse(): ClientErrorPageResponse {
        return this.clientErrorPageResponse
    }

    override fun setServerErrorPageResponse(serverErrorPageResponse: ServerErrorPageResponse) {
        this.serverErrorPageResponse = serverErrorPageResponse
    }

    override fun getServerErrorPageResponse(): ServerErrorPageResponse {
        return this.serverErrorPageResponse
    }
}