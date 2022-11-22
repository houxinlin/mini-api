import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.http.HttpIntercept
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse

class CookieTest {
}


fun main() {
    val miniContext = CoolMini(7070)
    miniContext.addHttpIntercept(object : HttpIntercept {
        override fun intercept(httpRequest: HttpRequest, httpResponse: HttpResponse): Boolean {
            return httpRequest.getSession().getAttibute("User", null) == null
        }

        override fun postHandler(httpRequest: HttpRequest, httpResponse: HttpResponse) {
        }
    }).addPathPatterns(arrayListOf("/**")).excludePathPatterns(arrayListOf("/set"))
    miniContext.start(CookieTest::class.java)
}