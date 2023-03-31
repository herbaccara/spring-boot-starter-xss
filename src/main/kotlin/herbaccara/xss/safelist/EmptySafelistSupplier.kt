package herbaccara.xss.safelist

import org.jsoup.safety.Safelist

class EmptySafelistSupplier : SafelistSupplier {

    override fun get(): Safelist? = null
}
