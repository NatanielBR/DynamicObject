import io.neoold.dynamic_object.DynamicObject.Companion.toDynamic
import org.junit.jupiter.api.Test
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
}