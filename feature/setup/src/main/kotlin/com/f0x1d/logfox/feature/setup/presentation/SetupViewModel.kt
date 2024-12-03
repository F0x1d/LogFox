package com.f0x1d.logfox.feature.setup.presentation

import android.Manifest
import android.app.Application
import com.f0x1d.logfox.arch.copyText
import com.f0x1d.logfox.arch.hardRestartApp
import com.f0x1d.logfox.arch.hasPermissionToReadLogs
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
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
): BaseViewModel<SetupState, SetupAction>(
    initialStateProvider = { SetupState() },
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
            sendAction(SetupAction.ShowSnackbar(Strings.no_root))
    }

    fun adb() = launchCatching {
        if (ctx.hasPermissionToReadLogs)
            gotPermission()
        else {
            reduce {
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
            sendAction(SetupAction.ShowSnackbar(Strings.shizuku_error))
    }

    fun checkPermission() = if (ctx.hasPermissionToReadLogs)
        gotPermission()
    else
        sendAction(SetupAction.ShowSnackbar(Strings.no_permission_detected))

    fun copyCommand() {
        ctx.copyText(adbCommand)
        sendAction(SetupAction.ShowSnackbar(Strings.text_copied))
    }

    fun closeAdbDialog() = reduce {
        copy(showAdbDialog = false)
    }

    private fun gotPermission() = ctx.hardRestartApp()
}
