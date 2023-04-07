package herbaccara.boot.autoconfigure.xss

import herbaccara.xss.annotation.XssFilter
import herbaccara.xss.filter.XssServletFilter
import jakarta.servlet.Filter
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@AutoConfiguration
@ConditionalOnClass(WebMvcConfigurer::class)
@EnableConfigurationProperties(XssFilterProperties::class)
class XssFilterAutoConfiguration(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    private val xssFilterProperties: XssFilterProperties
) {

    private fun patternsConditionUrls(mappingInfo: RequestMappingInfo): Set<String> {
        return mappingInfo.patternsCondition?.patterns ?: emptySet()
    }

    private fun pathPatternsConditionUrls(mappingInfo: RequestMappingInfo): Set<String> {
        return mappingInfo.pathPatternsCondition?.patterns?.map { it.patternString }?.toSet() ?: emptySet()
    }

    @Bean
    fun xssServletFilter(): FilterRegistrationBean<Filter> {
        val urlPatterns = requestMappingHandlerMapping.handlerMethods
            .flatMap { (mappingInfo, handlerMethod) ->
                val xssFilter = handlerMethod.getMethodAnnotation(XssFilter::class.java)
                    ?: AnnotatedElementUtils.findMergedAnnotation(handlerMethod.beanType, XssFilter::class.java)
                if (xssFilter != null) {
                    pathPatternsConditionUrls(mappingInfo) + patternsConditionUrls(mappingInfo)
                } else {
                    emptySet()
                }
            }

        return FilterRegistrationBean<Filter>().apply {
            filter = XssServletFilter(requestMappingHandlerMapping)
            order = xssFilterProperties.order
            addUrlPatterns(*urlPatterns.toTypedArray())
        }
    }
}
