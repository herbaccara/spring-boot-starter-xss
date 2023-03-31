package herbaccara.xss.safelist

import org.jsoup.safety.Safelist
import java.util.function.Supplier

interface SafelistSupplier : Supplier<Safelist?>
