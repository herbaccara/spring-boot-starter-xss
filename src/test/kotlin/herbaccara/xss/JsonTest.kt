package herbaccara.xss

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import herbaccara.xss.jackson.StringStdDeserializer
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.junit.jupiter.api.Test

class JsonTest {

    @Test
    fun test() {
        val clean: (String?) -> String? = {
            it?.let { Jsoup.clean(it, Safelist.none()).ifBlank { null } }
        }

        val objectMapper = jacksonObjectMapper().apply {
            findAndRegisterModules()
            registerModule(
                SimpleModule().apply {
                    addDeserializer(String::class.java, StringStdDeserializer(clean))
                }
            )
        }

//        val json = """
//            ["<script>1</scritp>", "<img>2"]
//        """.trimIndent()

        val json = """
            {
                "foo" : "<script>1</scritp>",
                "bar" : "<img>2",
                "asd" :  null
            }
        """.trimIndent()

//        val clean = Jsoup.clean(json, Safelist.none())
//        println(clean)
//
//        objectMapper.readTree(json)

        val obj = objectMapper.readValue<Any>(json)
        println(objectMapper.writeValueAsString(obj))
    }
}
