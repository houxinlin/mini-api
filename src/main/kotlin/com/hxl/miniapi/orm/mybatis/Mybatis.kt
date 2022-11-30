package com.hxl.miniapi.orm.mybatis

import org.apache.ibatis.executor.result.DefaultResultHandler
import org.apache.ibatis.executor.statement.StatementHandler
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.mapping.ResultMap
import org.apache.ibatis.mapping.SqlCommandType
import org.apache.ibatis.scripting.defaults.RawSqlSource
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.RowBounds
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.TransactionFactory
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.sql.Connection
import java.sql.PreparedStatement
import javax.sql.DataSource

class Mybatis(dataSource: DataSource) {
    private val transactionFactory: TransactionFactory = JdbcTransactionFactory()
    private val environment = Environment("development", transactionFactory, dataSource)
    private val configuration = Configuration(environment)
    private fun getSession() = MybatisAutoSessionProxy.threadLocal.get()
    private fun getConnection() = getSession().connection

    private fun getExecutor(connection: Connection) =
        configuration.newExecutor(transactionFactory.newTransaction(connection))

    init {
        configuration.isMapUnderscoreToCamelCase = true
    }

    fun openNewSession(): SqlSession {
        return getSqlSessionFactory().openSession(true)
    }

    /**
     * mybatis mapper代理对象的方法调用后要关闭sqlsession
     */
    class MapperMethodAutoSessionProxy<T>(private val mapper: T, val session: SqlSession) : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
            val result = if (args == null) {
                method.invoke(mapper)
            } else {
                method.invoke(mapper, *args.toList().toTypedArray())
            }
            session.close()
            return result
        }
    }

    fun <T> getMapper(type: Class<T>): T {
        if (!configuration.hasMapper(type)) configuration.addMapper(type)
        val session = openNewSession()
        val mapper = configuration.getMapper(type, session)
        //返回代理对象，用来关闭sqlsession
        return Proxy.newProxyInstance(
            ClassLoader.getSystemClassLoader(),
            arrayOf(type),
            MapperMethodAutoSessionProxy(mapper, session)
        ) as T
    }

    private fun applyParam(preparedStatement: PreparedStatement, vararg arg: Any) {
        for (index in arg.indices) {
            preparedStatement.setObject(index + 1, arg[index])
        }
    }

    fun <K, V> queryMap(querySql: String, vararg arg: Any): List<Map<K, V>>? {
        val connection = getConnection()
        val statementHandler = createStatementHandler(connection, querySql, Map::class.java, SqlCommandType.SELECT)
        val defaultResultHandler = DefaultResultHandler(configuration.objectFactory)
        val stmt = statementHandler.prepare(connection, 2000)
        applyParam(stmt as PreparedStatement, *arg)
        return statementHandler.query(stmt, defaultResultHandler)
    }


    fun <T> queryFormList(querySql: String, ofClass: Class<T>, vararg arg: Any): List<T>? {
        val connection = getConnection()
        val statementHandler = createStatementHandler(connection, querySql, ofClass, SqlCommandType.SELECT)
        val defaultResultHandler = DefaultResultHandler(configuration.objectFactory)
        val stmt = statementHandler.prepare(getConnection(), 2000)
        applyParam(stmt as PreparedStatement, *arg)
        return statementHandler.query<T>(stmt, defaultResultHandler)
    }

    fun update(querySql: String, vararg arg: Any): Int {
        val connection = getConnection()
        val statementHandler = createStatementHandler(connection, querySql, Int::class.java, SqlCommandType.UPDATE)
        val stmt = statementHandler.prepare(getConnection(), 2000)
        applyParam(stmt as PreparedStatement, *arg)
        return statementHandler.update(stmt)
    }

    private fun createResultMap(querySql: String, ofClass: Class<*>): ResultMap {
        return ResultMap.Builder(configuration, querySql, ofClass, mutableListOf())
            .build()
    }

    private fun createStatementHandler(
        connection: Connection,
        querySql: String,
        ofClass: Class<*>,
        sqlCommandType: SqlCommandType
    ): StatementHandler {
        val rawSqlSource = RawSqlSource(configuration, querySql, null)
        val statement = MappedStatement.Builder(configuration, querySql, rawSqlSource, sqlCommandType)
            .resultMaps(mutableListOf(createResultMap(querySql, ofClass)))
            .build()
        val boundSql = statement.getBoundSql(null)
        return configuration.newStatementHandler(getExecutor(connection), statement, null, RowBounds(), null, boundSql)
    }

    fun getSqlSessionFactory(): SqlSessionFactory = SqlSessionFactoryBuilder().build(configuration)

}