package com.hxl.miniapi.orm.mybatis

import com.hxl.miniapi.orm.CrudRepository

interface IMybatisCrudRepository : CrudRepository {
    fun <T> getMapper(type: Class<T>): T
    fun getMybatis(): Mybatis
}