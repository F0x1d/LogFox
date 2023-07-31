package com.f0x1d.logfox.viewmodel

import android.Manifest
import android.app.Application
import com.f0x1d.logfox.BuildConfig
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.hasPermissionToReadLogs
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.utils.terminal.DefaultTerminal
import com.f0x1d.logfox.utils.terminal.RootTerminal
import com.f0x1d.logfox.utils.terminal.ShizukuTerminal
import com.f0x1d.logfox.utils.terminal.shizukuAvailable
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    application: Application,
    private val appPreferences: AppPreferences,
    private val rootTerminal: RootTerminal,
    private val shizukuTerminal: ShizukuTerminal
): BaseViewModel(application) {

    private val command = arrayOf("pm", "grant", BuildConfig.APPLICATION_ID, Manifest.permission.READ_LOGS)
    val adbCommand = "adb shell ${command.joinToString(" ")}"

    companion object {
        const val EVENT_TYPE_GOT_PERMISSION = "got_permission"
        const val EVENT_TYPE_SHOW_ADB_DIALOG = "adb_dialog"
    }

    fun root() = launchCatching(Dispatchers.IO) {
        if (rootTerminal.isSupported()) {
            appPreferences.selectTerminal(RootTerminal.INDEX)

            rootTerminal.executeNow(*command)
            checkPermission()
        } else
            snackbar(R.string.no_root)
    }

    fun adb() = launchCatching(Dispatchers.IO) {
        if (ctx.hasPermissionToReadLogs())
            gotPermission()
        else {
            sendEvent(EVENT_TYPE_SHOW_ADB_DIALOG)
            appPreferences.selectTerminal(DefaultTerminal.INDEX)
        }
    }

    fun shizuku() = launchCatching(Dispatchers.IO) {
        appPreferences.selectTerminal(ShizukuTerminal.INDEX)

        if (shizukuAvailable && shizukuTerminal.isSupported() && shizukuTerminal.executeNow(*command).isSuccessful)
            gotPermission()
        else
            snackbar(R.string.shizuku_error)
    }

    fun checkPermission() = if (ctx.hasPermissionToReadLogs())
        gotPermission()
    else
        snackbar(R.string.no_permission_detected)

    private fun gotPermission() = sendEvent(EVENT_TYPE_GOT_PERMISSION)
}