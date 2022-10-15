import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.http.anno.GetMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.RequestBody

class ExceptionTest {
}
@RestController
class Test {
    @GetMapping("test")
    fun test(): String {
        throw NullPointerException("")
    }
}

fun main() {
    val miniContext = CoolMini(7070)
    miniContext.registerController(Test::class.java)
    miniContext.start(CookieTest::class.java)
}