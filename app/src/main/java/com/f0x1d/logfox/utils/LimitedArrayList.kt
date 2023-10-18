package com.f0x1d.logfox.utils

class LimitedArrayList<T>(capacity: Int): ArrayList<T>(capacity) {

    var capacity = capacity
        set(value) {
            if (value == field) return

            if (value < field)
                removeRange(0, this.capacity - capacity + 1)

            field = value
        }

    override fun add(element: T): Boolean {
        if (capacity <= 0) return false

        if (size >= capacity)
            removeRange(0, size - capacity + 1)

        return super.add(element)
    }
}