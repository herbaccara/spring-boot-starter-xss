package herbaccara.xss.annotation

import herbaccara.xss.validator.SafeHtmlValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [SafeHtmlValidator::class])
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD
)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class SafeHtml(
    val message: String = "Unsafe html content",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
