package com.hxl.miniapi.orm

interface CrudRepository {

    /**
     * @description: 查询单条数据
     * @date: 2022/10/6 上午4:35
     */

    fun <T> listOne(querySql: String, ofClass: Class<T>): T?


    /**
     * @description: 查询map
     * @date: 2022/10/6 上午4:35
     */

    fun listMap(querySql: String): List<Map<String, Any>>


    /**
     * @description: list数据
     * @date: 2022/10/6 上午4:36
     */

    fun <T> list(querySql: String, ofClass: Class<T>): List<T>


    /**
     * @description: 更新数据
     * @date: 2022/10/6 上午4:36
     */

    fun update(querySql: String): Int

}