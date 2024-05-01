package com.f0x1d.logfox.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.context.startLoggingAndServiceIfCan
import com.f0x1d.logfox.extensions.context.toast
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.utils.terminal.ShizukuTerminal
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver: BroadcastReceiver() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && appPreferences.startOnBoot) {
            if (appPreferences.selectedTerminalIndex == ShizukuTerminal.INDEX) {
                context.toast(R.string.shizuku_reminder)
            }

            context.startLoggingAndServiceIfCan(true)
        }
    }
}