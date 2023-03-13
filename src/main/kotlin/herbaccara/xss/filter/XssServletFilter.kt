package herbaccara.xss.filter

import herbaccara.xss.XssHttpServletRequestWrapper
import herbaccara.xss.annotation.XssFilter
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

class XssServletFilter(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping
) : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest) {
            val handlerExecutionChain = requestMappingHandlerMapping.getHandler(request)
            if (handlerExecutionChain != null) {
                val handler = handlerExecutionChain.handler
                if (handler is HandlerMethod) {
                    val xssFilter = handler.getMethodAnnotation(XssFilter::class.java)
                        ?: AnnotatedElementUtils.findMergedAnnotation(handler.beanType, XssFilter::class.java)
                    if (xssFilter != null) {
                        chain.doFilter(XssHttpServletRequestWrapper(request, xssFilter.level), response)
                        return
                    }
                }
            }
        } else {
            chain.doFilter(request, response)
        }
    }
}
