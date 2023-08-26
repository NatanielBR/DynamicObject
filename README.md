[![](https://jitpack.io/v/NatanielBR/DynamicObject.svg)](https://jitpack.io/#NatanielBR/DynamicObject)

DynamicObject it's a library who seeks be like dynamic keyword in C#.

# Install

Use jitpack tutorial to add this library in your project.

# Usage

Using this json by example:

````json
{
  "data": [
    {
      "name": "Peter",
      "age": 21
    },
    {
      "name": "Natan",
      "age": 22,
      "country": "Brazil"
    }
  ]
}
````

To transform this json in DynamicObject:

````kotlin

val dynamicObject = mapOf(
    "data" to listOf(
        mapOf(
            "name" to "Peter",
            "age" to 21,
        ),
        mapOf(
            "name" to "Natan",
            "age" to 22,
            "country" to "Brazil",
        ),
    )
)
````

To get:

- Data array:

````kotlin
dynamicObject["data"]!!.toTypeNullSafe<DynamicList>()
````

- Name of Peter:

````kotlin
dynamicObject["data"]!!.asDynamicList()[0].asDynamicObject()["name"]!!.toType<String>()
// Or using "comma operation"
dynamicObject["data", "0", "name"]?.toType<String>()
// Or using filter, using 'key?value'
dynamicObject["data", "name?Peter", "name"]?.toType<String>()
````

- Age of Natan:

````kotlin
dynamicObject["data"]!!.asDynamicList()[1].asDynamicObject()["age"]!!.toType<Int>()
// Or using "comma operation"
dynamicObject["data", "1", "age"]?.toType<Int>()
// Of using filter, using 'key?value'
dynamicObject["data", "name?Natan", "age"]?.toType<String>()
````

- get name of who have 'country' key:

````kotlin
dynamicObject["data"]?.asDynamicList()?.first { it.asDynamicObject().containsKey("country") }?.asDynamicObject()?.get("name")?.toType<String>()
// of using filter, using 'key?'
dynamicObject["data", "country?", "name"]?.toType<String>()
````

# Json to DynamicObject

````kotlin
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
"""

val dynamic = jsonText.jsonToDynamicObject().asDynamicObject()
````

Remember to call `asDynamicObject()` to get `DynamicObject` instance,
because a JSON have a `DynamicList` or `DynamicObject` as root.