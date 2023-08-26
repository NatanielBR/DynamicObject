import io.neoold.dynamic_object.DynamicJsonValue.Companion.jsonToDynamicObject
import io.neoold.dynamic_object.DynamicObject.Companion.toDynamic
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.expect

class TestLogic {

    @Test
    fun `Test filter logic`() {
        val dynamicObject = mapOf(
            "data" to listOf(
                mapOf(
                    "name" to "Peter",
                    "age" to 21,
                ),
                mapOf(
                    "name" to "Natan",
                    "age" to 22,
                    "country" to "Brazil"
                ),
            )
        ).toDynamic()

        expect("Peter") { dynamicObject["data", "name?Peter", "name"]?.toType<String>() }
        expect("22") { dynamicObject["data", "name?Natan", "age"]?.toType<String>() }
        expect("Natan") { dynamicObject["data", "country?", "name"]?.toType<String>() }
        expect("Natan") {
            dynamicObject["data"]?.asDynamicList()?.first { it.asDynamicObject().containsKey("country") }
                ?.asDynamicObject()?.get("name")?.toType<String>()
        }
    }

    @Test
    fun `Test Json Conversion`() {
        @Language("JSON") val jsonText = """
            {
                "data": {
                    "name": "Peter",
                    "age": 21,
                    "country": "Brazil",
                    "isAdmin": false
                },
                "error": null
            }
        """.trimIndent()

        val dynamic = jsonText.jsonToDynamicObject()

        val dynamicObject = runCatching { dynamic.asDynamicObject() }.getOrNull()
        assertNotNull(dynamicObject)

        assertEquals("Peter", dynamicObject["data", "name"]?.toType<String>())
        assertEquals(21, dynamicObject["data", "age"]?.toType<Int>())
        assertEquals(false, dynamicObject["data", "isAdmin"]?.toType<Boolean>())
        assertEquals(null, dynamicObject["error"]?.toType<Boolean>())
    }

    @Test
    fun `Test Json Conversion with array`() {
        @Language("JSON") val jsonText = """
            {
                "data": [
                    {
                        "name": "Peter",
                        "age": 16,
                        "country": "Brazil",
                        "isAdmin": false
                    },{
                        "name": "Natan",
                        "age": 23,
                        "country": "Brazil",
                        "isAdmin": true
                    }
                ],
                "error": null
            }
        """.trimIndent()

        val dynamic = jsonText.jsonToDynamicObject()

        val dynamicObject = runCatching { dynamic.asDynamicObject() }.getOrNull()
        assertNotNull(dynamicObject)

        assertEquals("Peter", dynamicObject["data","0", "name"]?.toType<String>())
        assertEquals(16, dynamicObject["data","0", "age"]?.toType<Int>())
        assertEquals(23, dynamicObject["data","1", "age"]?.toType<Int>())
        assertEquals(false, dynamicObject["data","0", "isAdmin"]?.toType<Boolean>())
        assertEquals(null, dynamicObject["error"]?.toType<Boolean>())
    }

    @Test
    fun `Test convert DynamicObject to Json String`() {
        val dynamicObject = mapOf(
            "data" to listOf(
                mapOf(
                    "name" to "Peter",
                    "age" to 21,
                ),
                mapOf(
                    "name" to "Natan",
                    "age" to 22,
                    "country" to "Brazil"
                ),
            )
        ).toDynamic()

        assertEquals(
            "{ \"data\": [{ \"name\": \"Peter\", \"age\": 21 }, { \"name\": \"Natan\", \"country\": \"Brazil\", \"age\": 22 }] }",
            dynamicObject.toJsonString()
        )
    }

    @Test
    fun `Test convert DynamicObject to Json String, but with value null`() {
        val dynamicObject = mapOf(
            "data" to listOf(
                mapOf(
                    "name" to "Peter",
                    "age" to 21,
                ),
                mapOf(
                    "name" to "Natan",
                    "age" to 22,
                    "country" to "Brazil"
                ),
            )
        ).toDynamic()
        dynamicObject["error"] = null

        assertEquals(
            "{ \"data\": [{ \"name\": \"Peter\", \"age\": 21 }, { \"name\": \"Natan\", \"country\": \"Brazil\", \"age\": 22 }], \"error\": null }",
            dynamicObject.toJsonString()
        )
    }
}