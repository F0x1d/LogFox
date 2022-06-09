package com.f0x1d.logfox.extensions

import androidx.lifecycle.MutableLiveData
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.utils.event.NoDataEvent

fun MutableLiveData<Event>.sendEvent(type: String, data: Any) {
    value = Event(type, data)
}
fun MutableLiveData<Event>.sendEvent(type: String) {
    value = NoDataEvent(type)
}