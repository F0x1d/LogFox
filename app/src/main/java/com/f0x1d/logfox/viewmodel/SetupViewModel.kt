package com.f0x1d.logfox.viewmodel

import android.Manifest
import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.BuildConfig
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.hasPermissionToReadLogs
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.utils.terminal.DefaultTerminal
import com.f0x1d.logfox.utils.terminal.RootTerminal
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    application: Application,
    private val appPreferences: AppPreferences,
    private val rootTerminal: RootTerminal
): BaseViewModel(application) {

    private val command = arrayOf("pm", "grant", BuildConfig.APPLICATION_ID, Manifest.permission.READ_LOGS)
    val adbCommand = "adb shell ${command.joinToString(" ")}"

    companion object {
        const val EVENT_TYPE_GOT_PERMISSION = "got_permission"
        const val EVENT_TYPE_SHOW_ADB_DIALOG = "adb_dialog"
    }

    fun root() = viewModelScope.launch(Dispatchers.IO) {
        if (rootTerminal.isSupported()) {
            rootTerminal.executeNow(*command)
            gotPermission(RootTerminal.INDEX)
        } else
            snackbar(R.string.no_root)
    }

    fun adb() = if (ctx.hasPermissionToReadLogs())
        gotPermission(DefaultTerminal.INDEX)
    else
        sendEvent(EVENT_TYPE_SHOW_ADB_DIALOG)

    fun checkPermission() = if (ctx.hasPermissionToReadLogs())
        gotPermission(DefaultTerminal.INDEX)
    else
        snackbar(R.string.no_permission_detected)

    private fun gotPermission(terminalIndex: Int) = sendEvent(EVENT_TYPE_GOT_PERMISSION).also {
        appPreferences.selectedTerminalIndex = terminalIndex
    }
}