import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.http.anno.PostMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.RequestUri
import com.hxl.miniapi.orm.AutowriteCrud
import com.hxl.miniapi.orm.MybatisCrudRepository
import com.hxl.miniapi.orm.MysqlDataSource
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

class DatabaseTest {
}

@RestController
class DatabaseTestController{
    interface  Mapper {
        @Select("select * from aunt_day where id =#{id}")
        fun list(@Param("id") id:Int);
    }
    @AutowriteCrud
    lateinit var mybatisCrudRepository: MybatisCrudRepository
    @PostMapping("test")
    fun test(@RequestUri string: String): String {
        println(mybatisCrudRepository.getMapper(Mapper::class.java).list(48))

        return string
    }
}
fun main() {
    val miniContext = CoolMini(7070)
    miniContext.registerController(DatabaseTestController::class.java)
    miniContext.setDataSource(MysqlDataSource("root", "qwe753-+", "jdbc:mysql://rm-bp1v2a7fy0qq145e5qo.mysql.rds.aliyuncs.com:3306/aunt-day"))

    miniContext.start(CookieTest::class.java)
}