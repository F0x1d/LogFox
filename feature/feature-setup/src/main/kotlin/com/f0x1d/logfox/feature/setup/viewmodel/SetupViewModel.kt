package com.f0x1d.logfox.feature.setup.viewmodel

import android.Manifest
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.context.copyText
import com.f0x1d.logfox.context.hasPermissionToReadLogs
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.terminals.DefaultTerminal
import com.f0x1d.logfox.terminals.RootTerminal
import com.f0x1d.logfox.terminals.ShizukuTerminal
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val rootTerminal: RootTerminal,
    private val shizukuTerminal: ShizukuTerminal,
    application: Application,
): BaseViewModel(application) {

    var showAdbDialog by mutableStateOf(false)

    val adbCommand get() = "adb shell ${command.joinToString(" ")}"
    private val command get() = arrayOf("pm", "grant", ctx.packageName, Manifest.permission.READ_LOGS)

    companion object {
        const val EVENT_TYPE_GOT_PERMISSION = "got_permission"
    }

    fun root() = launchCatching {
        if (rootTerminal.isSupported()) {
            appPreferences.selectTerminal(RootTerminal.INDEX)

            rootTerminal.executeNow(*command)
            checkPermission()
        } else
            snackbar(Strings.no_root)
    }

    fun adb() = launchCatching {
        if (ctx.hasPermissionToReadLogs)
            gotPermission()
        else {
            showAdbDialog = true
            appPreferences.selectTerminal(DefaultTerminal.INDEX)
        }
    }

    fun shizuku() = launchCatching {
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

    fun copyCommand() {
        ctx.copyText(adbCommand)
        snackbar(Strings.text_copied)
    }

    private fun gotPermission() = sendEvent(EVENT_TYPE_GOT_PERMISSION)
}
