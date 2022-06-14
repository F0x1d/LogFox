package com.f0x1d.logfox.viewmodel

import android.app.Application
import com.f0x1d.logfox.extensions.hasPermissionToReadLogs
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.extensions.startLoggingAndService
import com.f0x1d.logfox.repository.LoggingRepository
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(application: Application, private val loggingRepository: LoggingRepository): BaseViewModel(application) {

    companion object {
        const val EVENT_TYPE_SETUP = "setup"
    }

    init {
        load()
    }

    private fun load() {
        if (ctx.hasPermissionToReadLogs())
            ctx.startLoggingAndService(loggingRepository)
        else
            eventsData.sendEvent(EVENT_TYPE_SETUP)
    }
}