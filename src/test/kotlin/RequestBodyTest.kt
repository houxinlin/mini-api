import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.http.anno.GetMapping
import com.hxl.miniapi.http.anno.PostMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.RequestBody
import com.hxl.miniapi.http.response.HttpResponse

class RequestBodyTest {
}

@RestController
class RequestBodyTestController{
    @PostMapping("body")
    fun test(@RequestBody string: String,httpResponse: HttpResponse): String {
        httpResponse.getOutputStream().write("OK".toByteArray())
        return string
    }
}
fun main() {
    val miniContext = CoolMini(7070)
    miniContext.registerController(RequestBodyTestController::class.java)
    miniContext.start(CookieTest::class.java)
}