package com.f0x1d.logfox.viewmodel

import android.Manifest
import android.app.Application
import com.f0x1d.logfox.BuildConfig
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.hasPermissionToReadLogs
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(application: Application): BaseViewModel(application) {

    private val command = "pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.READ_LOGS}"
    val adbCommand = "adb shell $command"

    companion object {
        const val EVENT_TYPE_GOT_PERMISSION = "got_permission"
        const val EVENT_TYPE_SHOW_ADB_DIALOG = "adb_dialog"
    }

    fun root() = Shell.getShell { shell ->
        if (shell.isRoot) {
            Shell.cmd(command).exec()
            gotPermission()
            return@getShell
        }

        snackbar(R.string.no_root)
    }

    fun adb() = if (ctx.hasPermissionToReadLogs())
        gotPermission()
    else
        sendEvent(EVENT_TYPE_SHOW_ADB_DIALOG)

    fun checkPermission() = if (ctx.hasPermissionToReadLogs())
        gotPermission()
    else
        snackbar(R.string.no_root)

    private fun gotPermission() = sendEvent(EVENT_TYPE_GOT_PERMISSION)
}