package com.f0x1d.logfox.presentation

import android.app.Application
import android.content.Intent
import com.f0x1d.logfox.arch.hasPermissionToReadLogs
import com.f0x1d.logfox.arch.startForegroundServiceAvailable
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    application: Application,
) : BaseViewModel<MainState, MainAction>(
    initialStateProvider = { MainState },
    application = application,
) {
    var askedNotificationsPermission
        get() = appPreferences.askedNotificationsPermission
        set(value) { appPreferences.askedNotificationsPermission = value }

    val openCrashesOnStartup get() = appPreferences.openCrashesOnStartup

    init {
        load()
    }

    private fun load() {
        if (ctx.hasPermissionToReadLogs) {
            Intent(ctx, com.f0x1d.logfox.feature.logging.service.presentation.LoggingService::class.java).let {
                if (startForegroundServiceAvailable)
                    ctx.startForegroundService(it)
                else
                    ctx.startService(it)
            }
        } else {
            sendAction(MainAction.OpenSetup)
        }
    }
}
