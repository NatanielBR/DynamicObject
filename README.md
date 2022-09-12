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
      "age": 22
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
````

- Age of Natan:

````kotlin
dynamicObject["data"]!!.asDynamicList()[1].asDynamicObject()["age"]!!.toType<Int>()
// Or using "comma operation"
dynamicObject["data", "1", "age"]?.toType<Int>()
````
