package com.f0x1d.logfox.viewmodel

import android.app.Application
import com.f0x1d.logfox.extensions.context.hasPermissionToReadLogs
import com.f0x1d.logfox.extensions.context.startLoggingAndService
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.utils.DateTimeFormatter
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loggingRepository: LoggingRepository,
    private val appPreferences: AppPreferences,
    private val dateTimeFormatter: DateTimeFormatter,
    application: Application
): BaseViewModel(application) {

    companion object {
        const val EVENT_TYPE_SETUP = "setup"
    }

    var askedNotificationsPermission
        get() = appPreferences.askedNotificationsPermission
        set(value) { appPreferences.askedNotificationsPermission = value }

    init {
        load()
        dateTimeFormatter.startListening()
    }

    private fun load() = when (ctx.hasPermissionToReadLogs()) {
        true -> ctx.startLoggingAndService(loggingRepository, appPreferences)

        else -> sendEvent(EVENT_TYPE_SETUP)
    }

    override fun onCleared() {
        dateTimeFormatter.stopListening()
    }
}