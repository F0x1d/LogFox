package com.f0x1d.logfox.feature.preferences.presentation.service

import com.f0x1d.logfox.feature.terminals.base.TerminalType

internal sealed interface PreferencesServiceCommand {
    data object Load : PreferencesServiceCommand

    data class TerminalSelected(val type: TerminalType) : PreferencesServiceCommand
    data class StartOnBootChanged(val enabled: Boolean) : PreferencesServiceCommand
    data object ConfirmRestartLogging : PreferencesServiceCommand

    // Commands from effect handler
    data class PreferencesLoaded(
        val selectedTerminalType: TerminalType,
        val terminalNames: List<String>,
    ) : PreferencesServiceCommand

    data class TerminalSupported(val type: TerminalType) : PreferencesServiceCommand
    data object TerminalNotSupported : PreferencesServiceCommand
}
