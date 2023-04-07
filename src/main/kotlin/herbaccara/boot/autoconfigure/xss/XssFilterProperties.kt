package herbaccara.boot.autoconfigure.xss

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "xssfilter")
data class XssFilterProperties(
    val order: Int = 1
)