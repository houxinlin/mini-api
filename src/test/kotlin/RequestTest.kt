import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.http.anno.PostMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.RequestUri
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse

class RequestTest {
}
@RestController
class RequestTestController{
    @PostMapping("test")
    fun test(httpResponse: HttpResponse,httpRequest: HttpRequest) {
        println(httpRequest.getRequestPath())
        println(httpRequest.getHttpMethod())
        println(httpRequest.getContentType())
        println(httpRequest.getQueryString())
        println(httpRequest.getRequestPath())
        println(httpRequest.getUrl())

        httpResponse.getOutputStream().write("a".toByteArray())
    }
}
fun main() {
    println("a")
    val miniContext = CoolMini(7070)
    println("b")
  //  miniContext.registerController(RequestTestController::class.java)
  //  miniContext.start(CookieTest::class.java)
}