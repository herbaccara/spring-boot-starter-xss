package herbaccara.xss.filter

import herbaccara.xss.XssHttpServletRequestWrapper
import herbaccara.xss.annotation.DisabledXssFilter
import herbaccara.xss.annotation.XssFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

class XssServletFilter(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping
) : OncePerRequestFilter() {

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
                    filterChain.doFilter(XssHttpServletRequestWrapper(request, xssFilter.level), response)
                    return
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}
