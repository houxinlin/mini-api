package com.hxl.miniapi.utils

import com.hxl.miniapi.utils.StringUtils.tokenizeToStringArray
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

class AntPathMatcher {
    private var pathSeparator: String
    private var pathSeparatorPatternCache: PathSeparatorPatternCache
    private var caseSensitive = true
    private var trimTokens = false

    @Volatile
    private var cachePatterns: Boolean? = null
    private val tokenizedPatternCache: MutableMap<String, Array<String>?> = ConcurrentHashMap(256)
    private val stringMatcherCache: MutableMap<String, AntPathStringMatcher> = ConcurrentHashMap(256)

    constructor() {
        pathSeparator = DEFAULT_PATH_SEPARATOR
        pathSeparatorPatternCache = PathSeparatorPatternCache(DEFAULT_PATH_SEPARATOR)
    }

    constructor(pathSeparator: String) {
        this.pathSeparator = pathSeparator
        pathSeparatorPatternCache = PathSeparatorPatternCache(pathSeparator)
    }


    private fun deactivatePatternCache() {
        cachePatterns = false
        tokenizedPatternCache.clear()
        stringMatcherCache.clear()
    }

    fun match(pattern: String, path: String?): Boolean {
        return doMatch(pattern, path, true, null)
    }
    protected fun doMatch(
        pattern: String, path: String?, fullMatch: Boolean,
        uriTemplateVariables: MutableMap<String, String>?
    ): Boolean {
        if (path == null || path.startsWith(pathSeparator) != pattern.startsWith(pathSeparator)) {
            return false
        }
        val pattDirs = tokenizePattern(pattern)
        if (fullMatch && caseSensitive && !isPotentialMatch(path, pattDirs)) {
            return false
        }
        val pathDirs = tokenizePath(path)
        var pattIdxStart = 0
        var pattIdxEnd = pattDirs!!.size - 1
        var pathIdxStart = 0
        var pathIdxEnd = pathDirs.size - 1

        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            val pattDir = pattDirs[pattIdxStart]
            if ("**" == pattDir) {
                break
            }
            if (!matchStrings(pattDir, pathDirs[pathIdxStart], uriTemplateVariables)) {
                return false
            }
            pattIdxStart++
            pathIdxStart++
        }
        if (pathIdxStart > pathIdxEnd) {
            if (pattIdxStart > pattIdxEnd) {
                return pattern.endsWith(pathSeparator) == path.endsWith(pathSeparator)
            }
            if (!fullMatch) {
                return true
            }
            if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart] == "*" && path.endsWith(pathSeparator)) {
                return true
            }
            for (i in pattIdxStart..pattIdxEnd) {
                if (pattDirs[i] != "**") {
                    return false
                }
            }
            return true
        } else if (pattIdxStart > pattIdxEnd) {
            return false
        } else if (!fullMatch && "**" == pattDirs[pattIdxStart]) {
            return true
        }

        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            val pattDir = pattDirs[pattIdxEnd]
            if (pattDir == "**") {
                break
            }
            if (!matchStrings(pattDir, pathDirs[pathIdxEnd], uriTemplateVariables)) {
                return false
            }
            pattIdxEnd--
            pathIdxEnd--
        }
        if (pathIdxStart > pathIdxEnd) {
            for (i in pattIdxStart..pattIdxEnd) {
                if (pattDirs[i] != "**") {
                    return false
                }
            }
            return true
        }
        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            var patIdxTmp = -1
            for (i in pattIdxStart + 1..pattIdxEnd) {
                if (pattDirs[i] == "**") {
                    patIdxTmp = i
                    break
                }
            }
            if (patIdxTmp == pattIdxStart + 1) {
                pattIdxStart++
                continue
            }
            val patLength = patIdxTmp - pattIdxStart - 1
            val strLength = pathIdxEnd - pathIdxStart + 1
            var foundIdx = -1
            strLoop@ for (i in 0..strLength - patLength) {
                for (j in 0 until patLength) {
                    val subPat = pattDirs[pattIdxStart + j + 1]
                    val subStr = pathDirs[pathIdxStart + i + j]
                    if (!matchStrings(subPat, subStr, uriTemplateVariables)) {
                        continue@strLoop
                    }
                }
                foundIdx = pathIdxStart + i
                break
            }
            if (foundIdx == -1) {
                return false
            }
            pattIdxStart = patIdxTmp
            pathIdxStart = foundIdx + patLength
        }
        for (i in pattIdxStart..pattIdxEnd) {
            if (pattDirs[i] != "**") {
                return false
            }
        }
        return true
    }

    private fun isPotentialMatch(path: String, pattDirs: Array<String>?): Boolean {
        if (!trimTokens) {
            var pos = 0
            for (pattDir in pattDirs!!) {
                var skipped = skipSeparator(path, pos, pathSeparator)
                pos += skipped
                skipped = skipSegment(path, pos, pattDir)
                if (skipped < pattDir.length) {
                    return skipped > 0 || pattDir.length > 0 && isWildcardChar(pattDir[0])
                }
                pos += skipped
            }
        }
        return true
    }

    private fun skipSegment(path: String, pos: Int, prefix: String): Int {
        var skipped = 0
        for (i in 0 until prefix.length) {
            val c = prefix[i]
            if (isWildcardChar(c)) {
                return skipped
            }
            val currPos = pos + skipped
            if (currPos >= path.length) {
                return 0
            }
            if (c == path[currPos]) {
                skipped++
            }
        }
        return skipped
    }

    private fun skipSeparator(path: String, pos: Int, separator: String): Int {
        var skipped = 0
        while (path.startsWith(separator, pos + skipped)) {
            skipped += separator.length
        }
        return skipped
    }

    private fun isWildcardChar(c: Char): Boolean {
        for (candidate in WILDCARD_CHARS) {
            if (c == candidate) {
                return true
            }
        }
        return false
    }

    protected fun tokenizePattern(pattern: String): Array<String>? {
        var tokenized: Array<String>? = null
        val cachePatterns = this.cachePatterns
        if (cachePatterns == null || cachePatterns) {
            tokenized = tokenizedPatternCache[pattern]
        }
        if (tokenized == null) {
            tokenized = tokenizePath(pattern)
            if (cachePatterns == null && tokenizedPatternCache.size >= CACHE_TURNOFF_THRESHOLD) {
                deactivatePatternCache()
                return tokenized
            }
            if (cachePatterns == null || cachePatterns) {
                tokenizedPatternCache[pattern] = tokenized
            }
        }
        return tokenized
    }

    protected fun tokenizePath(path: String?): Array<String> {
        return tokenizeToStringArray(path, pathSeparator, trimTokens, true)
    }
    private fun matchStrings(
        pattern: String, str: String,
        uriTemplateVariables: MutableMap<String, String>?
    ): Boolean {
        return getStringMatcher(pattern).matchStrings(str, uriTemplateVariables)
    }
    protected fun getStringMatcher(pattern: String): AntPathStringMatcher {
        var matcher: AntPathStringMatcher? = null
        val cachePatterns = cachePatterns
        if (cachePatterns == null || cachePatterns) {
            matcher = stringMatcherCache[pattern]
        }
        if (matcher == null) {
            matcher = AntPathStringMatcher(pattern, caseSensitive)
            if (cachePatterns == null && stringMatcherCache.size >= CACHE_TURNOFF_THRESHOLD) {
                deactivatePatternCache()
                return matcher
            }
            if (cachePatterns == null || cachePatterns) {
                stringMatcherCache[pattern] = matcher
            }
        }
        return matcher
    }


    class AntPathStringMatcher @JvmOverloads constructor(
        private var  rawPattern: String?=null,
        private val caseSensitive: Boolean = true
    ) {
        private var exactMatch = false
        private var pattern: Pattern? = null
        private val variableNames: MutableList<String> = ArrayList()

        init {
            val patternBuilder = StringBuilder()
            val matcher = GLOB_PATTERN.matcher(rawPattern!!)
            var end = 0
            while (matcher.find()) {
                patternBuilder.append(quote(rawPattern!!, end, matcher.start()))
                val match = matcher.group()
                if ("?" == match) {
                    patternBuilder.append('.')
                } else if ("*" == match) {
                    patternBuilder.append(".*")
                } else if (match.startsWith("{") && match.endsWith("}")) {
                    val colonIdx = match.indexOf(':')
                    if (colonIdx == -1) {
                        patternBuilder.append(DEFAULT_VARIABLE_PATTERN)
                        variableNames.add(matcher.group(1))
                    } else {
                        val variablePattern = match.substring(colonIdx + 1, match.length - 1)
                        patternBuilder.append('(')
                        patternBuilder.append(variablePattern)
                        patternBuilder.append(')')
                        val variableName = match.substring(1, colonIdx)
                        variableNames.add(variableName)
                    }
                }
                end = matcher.end()
            }
            if (end == 0) {
                exactMatch = true
                pattern = null
            } else {
                exactMatch = false
                patternBuilder.append(quote(rawPattern!!, end, rawPattern!!.length))
                this.pattern = Pattern.compile(
                    patternBuilder.toString(),
                    Pattern.DOTALL or if (caseSensitive) 0 else Pattern.CASE_INSENSITIVE
                )
            }
        }

        private fun quote(s: String, start: Int, end: Int): String {
            return if (start == end) {
                ""
            } else Pattern.quote(s.substring(start, end))
        }

        fun matchStrings(str: String, uriTemplateVariables: MutableMap<String, String>?): Boolean {
            if (exactMatch) {
                return if (caseSensitive) rawPattern == str else rawPattern.equals(str, ignoreCase = true)
            } else if (rawPattern != null) {
                val matcher: Matcher = this.pattern!!.matcher(str)
                if (matcher.matches()) {
                    if (uriTemplateVariables != null) {
                        require(variableNames.size == matcher.groupCount()) {
                            ""
                        }
                        for (i in 1..matcher.groupCount()) {
                            val name = variableNames[i - 1]
                            require(!name.startsWith("*")) {
                                ""
                            }
                            val value = matcher.group(i)
                            uriTemplateVariables[name] = value
                        }
                    }
                    return true
                }
            }
            return false
        }

        companion object {
            private val GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}")
            private const val DEFAULT_VARIABLE_PATTERN = "((?s).*)"
        }
    }

    private class PathSeparatorPatternCache(pathSeparator: String) {
        val endsOnWildCard: String
        val endsOnDoubleWildCard: String

        init {
            endsOnWildCard = "$pathSeparator*"
            endsOnDoubleWildCard = "$pathSeparator**"
        }
    }

    companion object {
        const val DEFAULT_PATH_SEPARATOR = "/"
        private const val CACHE_TURNOFF_THRESHOLD = 65536
        private val WILDCARD_CHARS = charArrayOf('*', '?', '{')
    }
}