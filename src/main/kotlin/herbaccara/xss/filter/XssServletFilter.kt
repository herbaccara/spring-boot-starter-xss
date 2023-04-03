package herbaccara.xss.filter

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import herbaccara.xss.XssHttpServletRequestWrapper
import herbaccara.xss.annotation.DisabledXssFilter
import herbaccara.xss.annotation.XssFilter
import herbaccara.xss.jackson.StringStdDeserializer
import herbaccara.xss.safelist.SafelistSupplier
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.jsoup.safety.Safelist
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class XssServletFilter(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping
) : OncePerRequestFilter() {

    private val safelistSupplierMap: MutableMap<KClass<*>, SafelistSupplier> = mutableMapOf()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val handlerExecutionChain = requestMappingHandlerMapping.getHandler(request)
        if (handlerExecutionChain != null) {
            val handler = handlerExecutionChain.handler
            if (handler is HandlerMethod) {
                if (handler.hasMethodAnnotation(DisabledXssFilter::class.java)) {
                    filterChain.doFilter(request, response)
                    return
                }
                val xssFilter = handler.getMethodAnnotation(XssFilter::class.java)
                    ?: AnnotatedElementUtils.findMergedAnnotation(handler.beanType, XssFilter::class.java)
                if (xssFilter != null) {
                    val preserve = xssFilter.preserveRelativeLinks

                    val safelist = when (xssFilter.level) {
                        XssFilter.Level.NONE -> Safelist.none()
                        XssFilter.Level.SIMPLE_TEXT -> Safelist.simpleText()
                        XssFilter.Level.BASIC -> Safelist.basic()
                        XssFilter.Level.BASIC_WITH_IMAGES -> Safelist.basicWithImages().preserveRelativeLinks(preserve)
                        XssFilter.Level.RELAXED -> Safelist.relaxed().preserveRelativeLinks(preserve)
                        XssFilter.Level.CUSTOM -> {
                            safelistSupplierMap.getOrPut(xssFilter.safeListClass) {
                                xssFilter.safeListClass.createInstance()
                            }.get()
                        }
                    }

                    if (safelist != null) {
                        val objectMapper = jacksonObjectMapper().apply {
                            registerModule(
                                SimpleModule().apply {
                                    addDeserializer(String::class.java, StringStdDeserializer(safelist))
                                }
                            )
                        }

                        filterChain.doFilter(XssHttpServletRequestWrapper(request, objectMapper, safelist), response)
                        return
                    }
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}
