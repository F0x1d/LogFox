package com.f0x1d.logfox.utils.event

class NoDataEvent(type: String): Event(type, Unit) {

    override val type: String
        get() {
            isConsumed = true
            return super.type
        }
}