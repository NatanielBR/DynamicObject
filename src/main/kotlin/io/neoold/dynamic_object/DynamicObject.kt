package io.neoold.dynamic_object

import io.neoold.dynamic_object.DynamicValue.Companion.toDynamicValue

/**
 * Represents a dynamic object with Map in your base. Any Map with String as key and Any? as value will be
 * transformed to DynamicObject using Map<String, Any?>.toDynamic() extension.
 */
class DynamicObject constructor() : HashMap<String, DynamicValue>() {

    private constructor(value: Map<String, DynamicValue> = mapOf()) : this() {
        this.putAll(value.toMutableMap())
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
            obj = kotlin.runCatching {
                obj.value.let {
                    when (it) {
                        is DynamicObject -> {
                            it[key]
                        }

                        is DynamicList -> {
                            it[key.toInt()]
                        }

                        else -> {
                            null
                        }
                    }
                }
            }.getOrNull() ?: return null
        }

        return obj
    }

    operator fun set(key: String, any: Any) {
        super.put(key, any.toDynamicValue())
    }

    companion object {

        fun Map<String, Any?>.toDynamic(): DynamicObject {
            return DynamicObject(this.map { it.key to it.value.toDynamicValue() }.toMap())
        }
    }
}