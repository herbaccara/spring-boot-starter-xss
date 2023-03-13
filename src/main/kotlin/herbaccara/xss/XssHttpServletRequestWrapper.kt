package herbaccara.xss

import herbaccara.xss.annotation.XssFilter.Level
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import java.io.BufferedReader

class XssHttpServletRequestWrapper(
    request: HttpServletRequest,
    private val level: Level,
    private val jsonContentTypes: List<String> = listOf("application/json")
) : HttpServletRequestWrapper(request) {

    private fun selflist(): Safelist {
        return when (level) {
            Level.NONE -> Safelist.none()
            Level.SIMPLE_TEXT -> Safelist.simpleText()
            Level.BASIC -> Safelist.basic()
            Level.BASIC_WITH_IMAGES -> Safelist.basicWithImages()
            Level.RELAXED -> Safelist.relaxed()
        }
    }

    private fun clean(s: String): String = Jsoup.clean(s, selflist()).ifBlank { "" }

    override fun getParameter(name: String): String? {
        return super.getParameter(name)?.let(::clean)
    }

    override fun getParameterValues(name: String): Array<String>? {
        return super.getParameterValues(name)?.map(::clean)?.toTypedArray()
    }

    override fun getHeader(name: String): String? {
        return super.getHeader(name)?.let(::clean)
    }

    override fun getReader(): BufferedReader {
        val reader = super.getReader()
        if (jsonContentTypes.contains(super.getContentType())) {
            return inputStream.bufferedReader()
        }
        return reader
    }

    override fun getInputStream(): ServletInputStream {
        val stream = super.getInputStream()
        if (jsonContentTypes.contains(super.getContentType())) {
            return stream.use {
                val text = it.reader().readText().let(::clean)
                CachedServletInputStream(text.byteInputStream())
            }
        }
        return stream
    }
}
