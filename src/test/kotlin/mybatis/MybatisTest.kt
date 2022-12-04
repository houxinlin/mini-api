package mybatis


import com.hxl.miniapi.orm.mybatis.MysqlDataSource
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.TransactionFactory
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.LocalDateTypeHandler


class MybatisTest {
}

interface  Mapper {
    @Select("select * from tb_user")
    fun list():List<Map<String,Any>>
}
fun main() {
     val transactionFactory: TransactionFactory = JdbcTransactionFactory()
     val environment = Environment("development", transactionFactory, MysqlDataSource("root", "xx", "jdbc:mysql://localhost:3306/db_inner"))
     val configuration = Configuration(environment)

    configuration.isMapUnderscoreToCamelCase=true
    configuration.typeHandlerRegistry.register(Any::class.java, JdbcType.DATE,LocalDateTypeHandler())
    configuration.addMapper(Mapper::class.java)
    val build = SqlSessionFactoryBuilder().build(configuration).openSession().getMapper(Mapper::class.java)
    var list = build.list()

}