package com.f0x1d.logfox.extensions

import androidx.lifecycle.MutableLiveData
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.utils.event.NoDataEvent
import com.f0x1d.logfox.viewmodel.base.BaseViewModel

fun BaseViewModel.sendEvent(type: String, data: Any) = eventsData.sendEvent(type, data)
fun BaseViewModel.sendEvent(type: String) = eventsData.sendEvent(type)

fun MutableLiveData<Event>.sendEvent(type: String, data: Any) {
    postValue(Event(type, data))
}
fun MutableLiveData<Event>.sendEvent(type: String) {
    postValue(NoDataEvent(type))
}