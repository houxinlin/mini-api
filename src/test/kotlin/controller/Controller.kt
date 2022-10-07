package controller

import com.hxl.miniapi.http.anno.GetMapping
import com.hxl.miniapi.http.anno.PostMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.RequestParam
import com.hxl.miniapi.http.file.FilePart
import com.hxl.miniapi.http.session.Session

@RestController
class Controller {
    @GetMapping("login")
    fun login(session: Session):String{
        var attributeKeys = session.getAttributeKeys()
        return "name"
    }
    @PostMapping("set")
    fun set(@RequestParam("file") filePart: FilePart):String{
        return "ok"
    }
}