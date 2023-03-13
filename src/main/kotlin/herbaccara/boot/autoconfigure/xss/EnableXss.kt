package herbaccara.boot.autoconfigure.xss

import org.springframework.context.annotation.Import
import java.lang.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(XssAutoConfiguration::class)
annotation class EnableXss
