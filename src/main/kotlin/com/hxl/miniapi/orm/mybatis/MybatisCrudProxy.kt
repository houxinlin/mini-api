package com.hxl.miniapi.orm.mybatis

import com.hxl.miniapi.orm.CrudRepository
import org.apache.ibatis.session.SqlSession
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class MybatisCrudProxy(private val mybatisRepositoryReal: IMybatisCrudRepository) : InvocationHandler {
    companion object{
        val threadLocal = ThreadLocal<SqlSession>()
    }

    override fun invoke(proxy: Any?, method: Method, args: Array<*>?): Any {
        if(method.declaringClass == CrudRepository::class.java){
            threadLocal.set(mybatisRepositoryReal.getMybatis().getSqlSessionFactory().openSession(true))
        }
        val result = method.invoke(mybatisRepositoryReal,*args!!.toList().toTypedArray())
        threadLocal.get().close()
        return  result
    }
}