package com.f0x1d.logfox.feature.setup.presentation

import androidx.annotation.StringRes
import com.f0x1d.logfox.feature.terminals.api.base.TerminalType

internal sealed interface SetupSideEffect {
    // Business logic side effects (handled by EffectHandler)
    data object ExecuteRootCommand : SetupSideEffect
    data object ExecuteShizukuCommand : SetupSideEffect
    data class SelectTerminal(val type: TerminalType) : SetupSideEffect
    data class CopyAdbCommand(val command: String) : SetupSideEffect
    data object CheckPermission : SetupSideEffect
    data object CheckAdbPermission : SetupSideEffect

    // UI side effects (handled by Fragment)
    data class ShowSnackbar(@StringRes val textResId: Int) : SetupSideEffect
    data object RestartApp : SetupSideEffect
}
