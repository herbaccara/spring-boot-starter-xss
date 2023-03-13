package herbaccara.xss

import herbaccara.xss.annotation.XssFilter.Level
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.jsoup.Jsoup
import java.io.BufferedReader

class XssHttpServletRequestWrapper(
    request: HttpServletRequest,
    private val level: Level,
    private val jsonContentTypes: List<String> = listOf("application/json")
) : HttpServletRequestWrapper(request) {

    private fun clean(s: String): String = Jsoup.clean(s, level.safelist).ifBlank { "" }

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
