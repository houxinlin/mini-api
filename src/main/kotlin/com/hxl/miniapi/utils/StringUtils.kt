package com.hxl.miniapi.utils

import java.util.*

object StringUtils {
    private val EMPTY_STRING_ARRAY = arrayOf<String>()
    private const val FOLDER_SEPARATOR_CHAR = '/'
    private const val EXTENSION_SEPARATOR = '.'

    @Deprecated("as of 5.3, in favor of {@link #hasLength(String)} and")
    fun isEmpty(str: Any?): Boolean {
        return str == null || "" == str
    }
    private fun containsText(str: CharSequence): Boolean {
        val strLen = str.length
        for (i in 0 until strLen) {
            if (!Character.isWhitespace(str[i])) {
                return true
            }
        }
        return false
    }

    fun quote(str: String?): String? {
        return if (str != null) "'$str'" else null
    }

    fun getFilename(path: String?): String? {
        if (path == null) {
            return null
        }
        val separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR_CHAR)
        return if (separatorIndex != -1) path.substring(separatorIndex + 1) else path
    }
    fun getFilenameExtension(path: String?): String? {
        if (path == null) {
            return null
        }
        val extIndex = path.lastIndexOf(EXTENSION_SEPARATOR)
        if (extIndex == -1) {
            return null
        }
        val folderIndex = path.lastIndexOf(FOLDER_SEPARATOR_CHAR)
        return if (folderIndex > extIndex) {
            null
        } else path.substring(extIndex + 1)
    }

    private fun validateLocalePart(localePart: String) {
        for (i in 0 until localePart.length) {
            val ch = localePart[i]
            require(!((ch != ' ' && ch != '_' && ch != '-') && ch != '#' && !Character.isLetterOrDigit(ch))) { "Locale part \"$localePart\" contains invalid characters" }
        }
    }

    fun toStringArray(collection: Collection<String>?): Array<String> {
        return if (collection!!.isNotEmpty()){ collection.toTypedArray()} else EMPTY_STRING_ARRAY
    }


    @JvmStatic
    @JvmOverloads
    fun tokenizeToStringArray(
        str: String?, delimiters: String?, trimTokens: Boolean = true, ignoreEmptyTokens: Boolean = true
    ): Array<String> {
        if (str == null) {
            return EMPTY_STRING_ARRAY
        }
        val st = StringTokenizer(str, delimiters)
        val tokens: MutableList<String> = ArrayList()
        while (st.hasMoreTokens()) {
            var token = st.nextToken()
            if (trimTokens) {
                token = token.trim { it <= ' ' }
            }
            if (!ignoreEmptyTokens || token.length > 0) {
                tokens.add(token)
            }
        }
        var toStringArray = toStringArray(tokens)
        return toStringArray
    }

}