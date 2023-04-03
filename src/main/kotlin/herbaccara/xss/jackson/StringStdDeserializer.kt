package herbaccara.xss.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

class StringStdDeserializer(private val safelist: Safelist) : StdDeserializer<String>(String::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String? {
        return p.text?.let { Jsoup.clean(it, safelist) }
    }
}
