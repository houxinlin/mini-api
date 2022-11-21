package com.hxl.miniapi.orm.mybatis

import org.apache.ibatis.datasource.pooled.PooledDataSource

class MysqlDataSource(username: String, password: String, url: String) :
    PooledDataSource("com.mysql.cj.jdbc.Driver", url, username, password) {
    init {
    }
}