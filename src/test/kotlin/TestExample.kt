import io.neoold.dynamic_object.DynamicJsonValue.Companion.jsonToDynamicObject
import io.neoold.dynamic_object.DynamicList
import io.neoold.dynamic_object.DynamicObject
import io.neoold.dynamic_object.DynamicObject.Companion.toDynamic
import kotlin.test.Test
import kotlin.test.expect

class TestExample {

    @Test
    fun `DynamicObject method example test`() {
        val dynamic = DynamicObject()
        /*
        {
            "foo": {
                "bar": {
                    "foo_bar": 1,
                    "bar_foo": 2
                }
            },
            "baz": false
        }
         */
        dynamic["foo"] = mapOf(
            "bar" to mapOf(
                "foo_bar" to 1,
                "bar_foo" to 2,
            )
        )

        dynamic["baz"] = false
        
        /*
          dynamic["baz"] // DynamicValue<Boolean> -> false
          dynamic["foo", "bar"] // DynamicValue<DynamicObject>
          dynamic["foo", "bar", "foo_bar"] // DynamicValue<Int> -> 1
          dynamic["foo", "bar", "bar"] // null
          dynamic["bar"] // null
         */

        expect(true) { dynamic["baz"]?.isType<Boolean>() }
        expect(false) { dynamic["baz"]!!.toTypeNullSafe() }
        expect(true) { dynamic["foo", "bar"]?.isType<DynamicObject>() }
        expect(true) { dynamic["foo", "bar", "foo_bar"]?.isType<Int>() }
        expect(1) { dynamic["foo", "bar", "foo_bar"]!!.toTypeNullSafe() }
        expect(null) { dynamic["foo", "bar", "bar"] }
        expect(null) { dynamic["bar"] }
    }

    @Test
    fun `Json to DynamicObject test`() {
        val jsonText = """
            {
            "foo": {
                "bar": {
                    "foo_bar": 1,
                    "bar_foo": 2
                }
            },
            "baz": false
        }
        """.trimIndent()
        val dynamic = jsonText.jsonToDynamicObject().asDynamicObject()

        /*
          dynamic["baz"] // DynamicValue<Boolean> -> false
          dynamic["foo", "bar"] // DynamicValue<DynamicObject>
          dynamic["foo", "bar", "foo_bar"] // DynamicValue<Int> -> 1
          dynamic["foo", "bar", "bar"] // null
          dynamic["bar"] // null
         */

        expect(true) { dynamic["baz"]?.isType<Boolean>() }
        expect(false) { dynamic["baz"]!!.toTypeNullSafe() }
        expect(true) { dynamic["foo", "bar"]?.isType<DynamicObject>() }
        expect(true) { dynamic["foo", "bar", "foo_bar"]?.isType<Int>() }
        expect(1) { dynamic["foo", "bar", "foo_bar"]!!.toTypeNullSafe() }
        expect(null) { dynamic["foo", "bar", "bar"] }
        expect(null) { dynamic["bar"] }
    }

    @Test
    fun `Test Readme usage`() {
        val dynamicObject = mapOf(
            "data" to listOf(
                mapOf(
                    "name" to "Peter",
                    "age" to 21,
                ),
                mapOf(
                    "name" to "Natan",
                    "age" to 22,
                ),
            )
        ).toDynamic()

        expect(true) { dynamicObject["data"]?.isType<DynamicList>() }
        expect(false) { dynamicObject["data"]!!.toTypeNullSafe<DynamicList>().isEmpty() }
        expect("Peter") { dynamicObject["data"]!!.asDynamicList()[0].asDynamicObject()["name"]?.toType<String>() }
        expect("Peter") { dynamicObject["data", "0", "name"]?.toType<String>() }
        expect(21) { dynamicObject["data"]!!.asDynamicList()[0].asDynamicObject()["age"]?.toType<Int>() }
        expect(21) { dynamicObject["data", "0", "age"]?.toType<Int>() }
        expect("Natan") { dynamicObject["data"]!!.asDynamicList()[1].asDynamicObject()["name"]?.toType<String>() }
        expect("Natan") { dynamicObject["data", "1", "name"]?.toType<String>() }
        expect(22) { dynamicObject["data"]!!.asDynamicList()[1].asDynamicObject()["age"]?.toType<Int>() }
        expect(22) { dynamicObject["data", "1", "age"]?.toType<Int>() }
    }
}