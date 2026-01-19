package com.f0x1d.logfox.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.f0x1d.logfox.core.compat.startForegroundServiceAvailable
import com.f0x1d.logfox.core.context.hasPermissionToReadLogs
import com.f0x1d.logfox.core.context.toast
import com.f0x1d.logfox.feature.logging.service.presentation.LoggingService
import com.f0x1d.logfox.feature.preferences.domain.GetSelectedTerminalTypeUseCase
import com.f0x1d.logfox.feature.preferences.domain.GetStartOnBootUseCase
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var getStartOnBootUseCase: GetStartOnBootUseCase

    @Inject
    lateinit var getSelectedTerminalTypeUseCase: GetSelectedTerminalTypeUseCase

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && getStartOnBootUseCase()) {
            if (getSelectedTerminalTypeUseCase() == TerminalType.Shizuku) {
                context.toast(Strings.shizuku_reminder)
            }

            if (context.hasPermissionToReadLogs) {
                Intent(context, LoggingService::class.java).let {
                    if (startForegroundServiceAvailable) {
                        context.startForegroundService(it)
                    } else {
                        context.startService(it)
                    }
                }
            }
        }
    }
}
