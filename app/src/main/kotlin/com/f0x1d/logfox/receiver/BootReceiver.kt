package com.f0x1d.logfox.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.f0x1d.feature.logging.service.LoggingService
import com.f0x1d.logfox.R
import com.f0x1d.logfox.arch.startForegroundServiceAvailable
import com.f0x1d.logfox.context.hasPermissionToReadLogs
import com.f0x1d.logfox.context.toast
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver: BroadcastReceiver() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && appPreferences.startOnBoot) {
            if (appPreferences.selectedTerminalIndex == com.f0x1d.logfox.terminals.ShizukuTerminal.INDEX) {
                context.toast(R.string.shizuku_reminder)
            }

            if (context.hasPermissionToReadLogs) {
                Intent(context, LoggingService::class.java).let {
                    if (startForegroundServiceAvailable)
                        context.startForegroundService(it)
                    else
                        context.startService(it)
                }
            }
        }
    }
}
