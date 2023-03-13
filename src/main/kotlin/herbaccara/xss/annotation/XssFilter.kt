package herbaccara.xss.annotation

import java.lang.annotation.*

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class XssFilter(val level: Level = Level.NONE) {

    enum class Level {
        NONE, SIMPLE_TEXT, BASIC, BASIC_WITH_IMAGES, RELAXED
    }
}
