package io.neoold.dynamic_object

import io.neoold.dynamic_object.DynamicValue.Companion.toDynamicValue

/**
 * Easy Class to class List<DynamicValue>, without this class the way to get a same result is:
 *
 * dynamicValue.toType<List<*>>().map{ it as DynamicValue }
 *
 * or
 *
 * dynamicValue.toType<List<*>>().map{ it.yoDynamicValue() }
 *
 * And errors as DynamicValue<DynamicValue<Boolean>> may happen.
 */
class DynamicList private constructor(value: List<DynamicValue>) : ArrayList<DynamicValue>() {

    init {
        this.addAll(value.toMutableList())
    }

    companion object {
        fun List<Any?>.toDynamic(): DynamicList {
            return DynamicList(map { if (it is DynamicValue) it else it.toDynamicValue() })
        }
    }
}