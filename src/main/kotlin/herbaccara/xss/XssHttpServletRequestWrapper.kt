package herbaccara.xss

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.io.BufferedReader

class XssHttpServletRequestWrapper(
    request: HttpServletRequest,
    private val clean: (String?) -> String?,
    private val objectMapper: ObjectMapper,
    private val jsonContentTypes: List<String> = listOf("application/json")
) : HttpServletRequestWrapper(request) {

    override fun getParameter(name: String): String? {
        return super.getParameter(name)?.let(clean)
    }

    override fun getParameterValues(name: String): Array<String?>? {
        return super.getParameterValues(name)?.map(clean)?.toTypedArray()
    }

    override fun getHeader(name: String): String? {
        return super.getHeader(name)?.let(clean)
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
                val obj = objectMapper.readValue<Any>(it.reader().readText())
                val json = objectMapper.writeValueAsString(obj)
                CachedServletInputStream(json.byteInputStream())
            }
        }
        return stream
    }
}
