package io.neoold.dynamic_object

import io.neoold.dynamic_object.DynamicList.Companion.toDynamic
import io.neoold.dynamic_object.DynamicObject.Companion.toDynamic
import io.neoold.dynamic_object.DynamicObject.Companion.toDynamicJsonObject
import io.neoold.dynamic_object.DynamicValue.Companion.toDynamicValue
import kotlinx.serialization.json.*

class DynamicJsonValue(
    valueJson: JsonElement
) : DynamicValue(valueJson) {
    init {
        // try to convert to object
        runCatching {
            val obj = valueJson.jsonObject
            value = obj.map { it.key to it.value }.toMap().toDynamicJsonObject()
        }.onFailure {
            // try to convert to list
            runCatching {
                val list = valueJson.jsonArray
                value = list.toList().map { it.toDynamicJsonValue() }.toDynamic()
            }.onFailure {
                // do primitive
                value = castJsonPrimitive(valueJson.jsonPrimitive)
            }
        }
    }

    companion object {
        /**
         * Cast a JsonPrimitive to a primitive type
         */
        private fun castJsonPrimitive(primitive: JsonPrimitive): Any? {
            return if (primitive.isString) primitive.content else primitive.intOrNull
                ?: primitive.longOrNull
                ?: primitive.doubleOrNull
                ?: primitive.floatOrNull
                ?: primitive.booleanOrNull
        }


        /**
         * Convert a String Json to a DynamicValue.
         */
        fun String.jsonToDynamicObject(): DynamicValue {
            return DynamicJsonValue(
                Json.decodeFromString<JsonElement>(this)
            )
        }

        /**
         * Convert a JsonElement to a DynamicValue.
         */
        private fun JsonElement.toDynamicJsonValue(): DynamicValue {
            return DynamicJsonValue(this)
        }
    }

}