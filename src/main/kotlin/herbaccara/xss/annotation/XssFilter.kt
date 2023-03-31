package herbaccara.xss.annotation

import herbaccara.xss.safelist.EmptySafelistSupplier
import herbaccara.xss.safelist.SafelistSupplier
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class XssFilter(
    val level: Level = Level.NONE,
    val preserveRelativeLinks: Boolean = false,
    val safeListClass: KClass<out SafelistSupplier> = EmptySafelistSupplier::class
) {

    enum class Level {
        NONE,
        SIMPLE_TEXT,
        BASIC,
        BASIC_WITH_IMAGES,
        RELAXED,
        CUSTOM
    }
}
