package com.f0x1d.logfox.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.f0x1d.feature.logging.service.LoggingService
import com.f0x1d.logfox.arch.hasPermissionToReadLogs
import com.f0x1d.logfox.arch.startForegroundServiceAvailable
import com.f0x1d.logfox.arch.toast
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.terminals.ShizukuTerminal
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver: BroadcastReceiver() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && appPreferences.startOnBoot) {
            if (appPreferences.selectedTerminalIndex == ShizukuTerminal.INDEX) {
                context.toast(Strings.shizuku_reminder)
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
