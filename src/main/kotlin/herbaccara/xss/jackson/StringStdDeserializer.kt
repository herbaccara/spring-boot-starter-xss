package herbaccara.xss.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class StringStdDeserializer(private val clean: (String?) -> String?) : StdDeserializer<String>(String::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String? {
        return p.text?.let(clean)
    }
}
