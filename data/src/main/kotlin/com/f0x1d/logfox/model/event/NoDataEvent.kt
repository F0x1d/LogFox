package com.f0x1d.logfox.model.event

class NoDataEvent(type: String): Event(type, Unit) {

    override val type: String
        get() {
            isConsumed = true
            return super.type
        }
}
