package io.neoold.dynamic_object

import io.neoold.dynamic_object.DynamicList.Companion.toDynamic
import io.neoold.dynamic_object.DynamicObject.Companion.toDynamic
import java.util.*

/**
 * Represents a value without type defined. This class use Any type and unsafe casts to transform
 * into desired type. To create a DynamicValue, just do instance this object or use extension method:
 * Any.toDynamicValue()
 */
open class DynamicValue(var value: Any?) {

    init {
        if (value is Map<*, *>) {
            value = (value as Map<*, *>).map { it.key as String to it.value }.toMap().toDynamic()
        } else if (value is List<*>) {
            value = (value as List<*>).toDynamic()
        }
    }

    /**
     * Method to check if Generic type is same type as value.
     */
    inline fun <reified T> isType(): Boolean {
        return kotlin.runCatching { toType<T>() }.isSuccess
    }

    /**
     * Easy method to cast value to DynamicObject. This method is not cast safe, if value
     * is not DynamicObject type, a ClassCastException will be thrown.
     */
    fun asDynamicObject(): DynamicObject {
        return toTypeNullSafe()
    }

    /**
     * Easy method to cast value to DynamicList. This method is not cast safe, if value
     * is not DynamicList type, a ClassCastException will be thrown.
     */
    fun asDynamicList(): DynamicList {
        return toTypeNullSafe()
    }

    /**
     * Easy method to cast value to Generic Type, T, and check null safe using !! operador.
     */
    inline fun <reified T> toTypeNullSafe(): T {
        return toType<T>()!!
    }

    /**
     * Will cast value to Generic type, T. This method is not cast safe.
     *
     * @return T if cast was success or null if value is null or unknown type. To others type, use DynamicValue.value as MyClass to cast to your class.
     */
    inline fun <reified T> toType(): T? {
        if (value == null) {
            return null
        }
        return when (T::class) {
            String::class -> value.toString()
            Long::class -> {
                if (value is Int) {
                    (value as Int).toLong()
                } else {
                    (value as Long)
                }
            }

            Int::class -> value as Int
            Boolean::class -> value as Boolean
            Double::class -> {
                if (value is Float) {
                    (value as Float).toDouble()
                } else {
                    value as Double
                }
            }

            Float::class -> value as Float
            DynamicObject::class -> {
                if (value is DynamicObject) {
                    value
                } else {
                    value = (value as Map<*, *>).map { it.key as String to it.value }.toMap().toDynamic()
                    value
                }
            }

            DynamicList::class -> {
                if (value is DynamicList) {
                    value
                } else {
                    value = (value as List<*>).toDynamic()
                    value
                }
            }

            else -> {
                throw Exception("Invalid type")
            }
        } as T?
    }

    override fun equals(other: Any?): Boolean {
        return toType<String>() == other
    }

    override fun hashCode(): Int {
        return Objects.hashCode(value)
    }

    companion object {

        /**
         * Convert any type to DynamicValue.
         */
        fun Any?.toDynamicValue(): DynamicValue {
            return DynamicValue(this)
        }
    }
}