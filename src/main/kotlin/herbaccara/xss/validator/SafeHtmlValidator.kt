package herbaccara.xss.validator

import herbaccara.xss.annotation.SafeHtml
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

class SafeHtmlValidator : ConstraintValidator<SafeHtml, String> {

    override fun isValid(value: String?, ctx: ConstraintValidatorContext): Boolean {
        return value == null || Jsoup.isValid(value, Safelist.none())
    }
}
