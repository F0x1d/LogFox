package com.f0x1d.logfox.feature.setup.viewmodel

import android.Manifest
import android.app.Application
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.context.hasPermissionToReadLogs
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.terminals.DefaultTerminal
import com.f0x1d.logfox.terminals.RootTerminal
import com.f0x1d.logfox.terminals.ShizukuTerminal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val rootTerminal: RootTerminal,
    private val shizukuTerminal: ShizukuTerminal,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel(application) {

    private val command get() = arrayOf("pm", "grant", ctx.packageName, Manifest.permission.READ_LOGS)
    val adbCommand get() = "adb shell ${command.joinToString(" ")}"

    companion object {
        const val EVENT_TYPE_GOT_PERMISSION = "got_permission"
        const val EVENT_TYPE_SHOW_ADB_DIALOG = "adb_dialog"
    }

    fun root() = launchCatching(ioDispatcher) {
        if (rootTerminal.isSupported()) {
            appPreferences.selectTerminal(RootTerminal.INDEX)

            rootTerminal.executeNow(*command)
            checkPermission()
        } else
            snackbar(Strings.no_root)
    }

    fun adb() = launchCatching(ioDispatcher) {
        if (ctx.hasPermissionToReadLogs)
            gotPermission()
        else {
            sendEvent(EVENT_TYPE_SHOW_ADB_DIALOG)
            appPreferences.selectTerminal(DefaultTerminal.INDEX)
        }
    }

    fun shizuku() = launchCatching(ioDispatcher) {
        appPreferences.selectTerminal(ShizukuTerminal.INDEX)

        if (shizukuTerminal.isSupported() && shizukuTerminal.executeNow(*command).isSuccessful)
            gotPermission()
        else
            snackbar(Strings.shizuku_error)
    }

    fun checkPermission() = if (ctx.hasPermissionToReadLogs)
        gotPermission()
    else
        snackbar(Strings.no_permission_detected)

    private fun gotPermission() = sendEvent(EVENT_TYPE_GOT_PERMISSION)
}
