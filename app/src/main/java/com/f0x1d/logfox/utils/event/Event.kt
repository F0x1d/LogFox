package com.f0x1d.logfox.utils.event

open class Event(open val type: String, private val data: Any) {
    var isConsumed = false
        protected set

    fun <T> consume(): T? {
        if (isConsumed)
            return null

        isConsumed = true
        return data as T
    }
}