package com.f0x1d.logfox.feature.setup.viewmodel

import android.Manifest
import android.app.Application
import com.f0x1d.logfox.arch.copyText
import com.f0x1d.logfox.arch.hardRestartApp
import com.f0x1d.logfox.arch.hasPermissionToReadLogs
import com.f0x1d.logfox.arch.viewmodel.BaseStateViewModel
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.SetupScreenState
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
): BaseStateViewModel<SetupScreenState>(
    initialStateProvider = { SetupScreenState() },
    application = application,
) {

    private val adbCommand get() = "adb shell ${command.joinToString(" ")}"
    private val command get() = arrayOf("pm", "grant", ctx.packageName, Manifest.permission.READ_LOGS)

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
            state {
                copy(
                    showAdbDialog = true,
                    adbCommand = this@SetupViewModel.adbCommand,
                )
            }

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

    fun closeAdbDialog() = state {
        copy(showAdbDialog = false)
    }

    private fun gotPermission() = ctx.hardRestartApp()
}
