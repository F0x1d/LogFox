package com.f0x1d.logfox.viewmodel.base

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.f0x1d.logfox.utils.event.Event

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {

    protected val ctx: Context
        get() = getApplication()

    val eventsData = MutableLiveData<Event>()
}