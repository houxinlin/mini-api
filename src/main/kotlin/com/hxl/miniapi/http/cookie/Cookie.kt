package com.hxl.miniapi.http.cookie

/**
 * @description: cookie
 * @date: 2022/10/10 上午3:52
 */

class Cookie(var name: String, var value: String) {


    /**
     * Cookie version value. `;Version=1 ...` means RFC 2109 style.
     */
    var version = 0

    /**
     * `;Comment=VALUE ...` describes cookie's use.
     */
    var comment: String? = null

    /**
     * `;Domain=VALUE ...` domain that sees cookie
     */
    var domain: String? = null

    /**
     * `;Max-Age=VALUE ...` cookies auto-expire
     */
    var maxAge = -1

    /**
     * `;Path=VALUE ...` URLs that see the cookie
     */
    var path: String? = null

    /**
     * `;Secure ...` e.g. use SSL
     */
    var secure = false

    /**
     * Not in cookie specs, but supported by browsers.
     */
    var httpOnly = false

    override fun toString(): String {
        return generateHeader() ?: ""
    }

    private fun generateHeader(): String? {

        val buf = StringBuffer()

        buf.append("$name=$value")

        if (version == 1) {
            buf.append("; Version=1")
            if (comment != null) {
                buf.append("; Comment=$comment")
            }
        }
        domain?.run { buf.append("; Domain=$domain") }


        if (maxAge >= 0) {
            if (version > 0) {
                buf.append("; Max-Age=")
                buf.append(maxAge)
            }
        }
        path?.run { buf.append("; Path=$path") }

        if (secure) buf.append("; Secure")
        if (httpOnly) buf.append("; HttpOnly")
        return buf.toString()
    }
}