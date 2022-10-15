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
        fun list(@Param("id") id:Int):List<Any>
    }
    @AutowriteCrud
    lateinit var mybatisCrudRepository: MybatisCrudRepository
    @PostMapping("test")
    fun test(@RequestUri string: String): String {
        println(mybatisCrudRepository.listMap("select * from aunt_day where id=? or id =?",48,11212))
        return string
    }
}
fun main() {
    val miniContext = CoolMini(7070)
    miniContext.registerController(DatabaseTestController::class.java)
    miniContext.setDataSource(MysqlDataSource("root", "qwe753-+", "jdbc:mysql://xxx"))

    miniContext.start(CookieTest::class.java)
}