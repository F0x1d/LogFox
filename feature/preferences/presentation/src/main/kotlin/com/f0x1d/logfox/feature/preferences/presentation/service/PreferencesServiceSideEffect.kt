package com.f0x1d.logfox.feature.preferences.presentation.service

import com.f0x1d.logfox.feature.terminals.base.TerminalType

internal sealed interface PreferencesServiceSideEffect {
    // Business logic side effects - handled by EffectHandler
    data object LoadPreferences : PreferencesServiceSideEffect
    data class CheckTerminalSupport(val type: TerminalType) : PreferencesServiceSideEffect
    data class SaveTerminalType(val type: TerminalType) : PreferencesServiceSideEffect
    data object RestartLogging : PreferencesServiceSideEffect

    // UI side effects - handled by Fragment
    data object ShowTerminalRestartDialog : PreferencesServiceSideEffect
    data object ShowTerminalUnavailableToast : PreferencesServiceSideEffect
    data object ShowAndroid13WarningDialog : PreferencesServiceSideEffect
}
