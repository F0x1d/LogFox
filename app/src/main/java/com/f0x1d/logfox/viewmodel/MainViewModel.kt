package com.f0x1d.logfox.viewmodel

import android.app.Application
import com.f0x1d.logfox.extensions.hasPermissionToReadLogs
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.extensions.startLoggingAndService
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(application: Application,
                                        private val loggingRepository: LoggingRepository,
                                        private val appPreferences: AppPreferences): BaseViewModel(application) {

    companion object {
        const val EVENT_TYPE_SETUP = "setup"
    }

    var askedNotificationsPermission
        get() = appPreferences.askedNotificationsPermission
        set(value) { appPreferences.askedNotificationsPermission = value }

    init {
        load()
    }

    private fun load() {
        if (ctx.hasPermissionToReadLogs())
            ctx.startLoggingAndService(loggingRepository)
        else
            sendEvent(EVENT_TYPE_SETUP)
    }
}