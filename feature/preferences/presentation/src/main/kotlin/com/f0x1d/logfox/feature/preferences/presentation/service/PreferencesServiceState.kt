package com.f0x1d.logfox.feature.preferences.presentation.service

import com.f0x1d.logfox.feature.terminals.api.base.TerminalType

internal data class PreferencesServiceState(
    val selectedTerminalType: TerminalType,
    val terminalNames: List<String>,
)
