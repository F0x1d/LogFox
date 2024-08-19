package com.f0x1d.logfox.viewmodel

import android.app.Application
import android.content.Intent
import com.f0x1d.feature.logging.service.LoggingService
import com.f0x1d.logfox.arch.hasPermissionToReadLogs
import com.f0x1d.logfox.arch.startForegroundServiceAvailable
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.arch.viewmodel.Event
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    application: Application,
): BaseViewModel(application) {

    var askedNotificationsPermission
        get() = appPreferences.askedNotificationsPermission
        set(value) { appPreferences.askedNotificationsPermission = value }

    val openCrashesOnStartup get() = appPreferences.openCrashesOnStartup

    init {
        load()
    }

    private fun load() {
        if (ctx.hasPermissionToReadLogs) {
            Intent(ctx, LoggingService::class.java).let {
                if (startForegroundServiceAvailable)
                    ctx.startForegroundService(it)
                else
                    ctx.startService(it)
            }
        } else {
            sendEvent(OpenSetup)
        }
    }
}

data object OpenSetup : Event
