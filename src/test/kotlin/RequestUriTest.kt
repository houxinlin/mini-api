import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.http.anno.PostMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.RequestUri

class RequestUriTest {
}
@RestController
class RequestUriTestController{
    @PostMapping("test")
    fun test(@RequestUri string: String): String {
        return string
    }
}
fun main() {
    val miniContext = CoolMini(7070)
    miniContext.registerController(RequestUriTestController::class.java)
    miniContext.start(CookieTest::class.java)
}