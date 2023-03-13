package herbaccara.xss.annotation

import org.jsoup.safety.Safelist
import java.lang.annotation.*

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class XssFilter(val level: Level = Level.NONE) {

    enum class Level(val safelist: Safelist) {
        NONE(Safelist.none()),
        SIMPLE_TEXT(Safelist.simpleText()),
        BASIC(Safelist.basic()),
        BASIC_WITH_IMAGES(Safelist.basicWithImages()),
        RELAXED(Safelist.relaxed())
    }
}
