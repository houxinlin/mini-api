package com.hxl.miniapi.orm


/**
* @description: Mybatis查询
* @date: 2022/10/6 上午6:25
*/

class MybatisCrudRepository(private val mybatis: Mybatis) : BaseCrudRepository() {

    fun  <T> getMapper(type:Class<T>):T{
        return mybatis.getMapper(type)
    }
    override fun <T> listOne(querySql: String, ofClass: Class<T>): T? {
        val list = mybatis.queryFormList(querySql, ofClass) ?: return null
        if (list.isNotEmpty()) return list[0]
        return null
    }

    override fun listMap(querySql: String): List<Map<String, Any>> {
        return mybatis.queryMap(querySql) ?: return mutableListOf()
    }

    override fun <T> list(querySql: String, ofClass: Class<T>): List<T> {
        return mybatis.queryFormList(querySql, ofClass) ?: return mutableListOf()
    }

    override fun update(querySql: String): Int {
        return mybatis.update(querySql)
    }
}