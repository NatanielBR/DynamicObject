package io.neoold.dynamic_object

import io.neoold.dynamic_object.DynamicValue.Companion.toDynamicValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Represents a dynamic object with Map in your base. Any Map with String as key and Any? as value will be
 * transformed to DynamicObject using Map<String, Any?>.toDynamic() extension.
 */
@Serializable
class DynamicObject() : HashMap<String, DynamicValue>() {

    private constructor(value: Map<String, DynamicValue> = mapOf()) : this() {
        this.putAll(value.toMutableMap())
    }

    /**
     * Check if selector matchs in this DynamicValue.
     * If selector contains ? or !, this method will check if value is DynamicList
     * or DynamicObject. After, this method will check if key is same and, if exists,
     * the value is same. If all are true, this method return the DynamicValue.
     *
     * If selector not contains ? or !, will get a key or index of DynamicValue.
     */
    private fun DynamicValue.match(selector: String): DynamicValue? {
        val value = this.value

        if (selector.contains("?") || selector.contains("!")) {
            // find by key?value

            val isFirst = selector.contains("?")
            val parts = if (isFirst) selector.split("?", limit = 2) else selector.split("!", limit = 2)

            when (value) {
                is DynamicObject -> {
                    return if (value.containsKey(parts[0])) {
                        if (parts[1].isNotEmpty() && value[parts[0]]?.equals(parts[1]) == true) {
                            this
                        } else if (parts[1].isEmpty()) {
                            this
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }

                is DynamicList -> {
                    return if (isFirst) {
                        value.firstOrNull { it.match(selector) != null }
                    } else {
                        value.lastOrNull { it.match(selector) != null }
                    }
                }

                else -> {
                    return if (equals(selector)) this else null
                }
            }

        } else {
            // find by key or index

            return when (value) {
                is DynamicObject -> {
                    value[selector]
                }

                is DynamicList -> {
                    value[selector.toInt()]
                }

                else -> {
                    null
                }
            }
        }
    }

    /**
     * Easy method to get a many attributes of DynamicObject values. For example:
     * JSON:
     * {
     *      "foo": {
     *          "bar": {
     *              "foo_bar": 1,
     *              "bar_foo": 2
     *          }
     *      }
     *      "baz": false
     * }
     *
     * In DynamicObject:
     *  dynamic["baz"] // DynamicValue<Boolean> -> false
     *  dynamic["foo", "bar"] // DynamicValue<DynamicObject>
     *  dynamic["foo", "bar", "foo_bar"] // DynamicValue<Int> -> 1
     *  dynamic["foo", "bar", "bar"] // null
     *  dynamic["bar"] // null
     */
    operator fun get(vararg keys: String): DynamicValue? {
        var obj: DynamicValue = kotlin.runCatching { this[keys.first()] }.getOrNull() ?: return null

        keys.toList().drop(1).forEach { key ->
            obj = obj.match(key) ?: return null
        }

        return obj
    }

    /**
     * Way to change value in DynamicObject.
     * Don't send DynamicObject or DynamicList type, will throw IllegalArgumentException.
     *
     * Instead, send Map or List.
     *
     * @throws IllegalArgumentException if any is DynamicObject or DynamicList.
     */
    operator fun set(key: String, any: Any?) {
        if (any is DynamicValue) {
            super.put(key, any)
        } else if (any is DynamicObject) {
            throw IllegalArgumentException("Invalid type: DynamicObject. Use Map instead.")
        } else if (any is DynamicList) {
            throw IllegalArgumentException("Invalid type: DynamicList. Use List instead.")
        } else {
            super.put(key, any.toDynamicValue())
        }
    }

    fun toJsonString(): String {
        return StringBuilder().also {
            it.append("{ ")
            val lastKey = this.keys.last()
            this.forEach { (key, value) ->
                it.append("\"$key\": ${value.toJsonString()}")
                if (key != lastKey) {
                    it.append(", ")
                }
            }
            it.append(" }").toString()
        }.toString()
    }

    companion object {

        /**
         * Convert a Map<String, Any?> to DynamicObject.
         */
        fun Map<String, Any?>.toDynamic(): DynamicObject {
            return DynamicObject(this.map { it.key to it.value.toDynamicValue() }.toMap())
        }

        /**
         * Convert a Map<String, JsonElement> to DynamicObject. Any? its different of JsonElement?, so
         * need to convert to DynamicJsonValue before.
         */
        fun Map<String, JsonElement>.toDynamicJsonObject(): DynamicObject {
            return DynamicObject(this.map { it.key to DynamicJsonValue(it.value) }.toMap())
        }
    }
}