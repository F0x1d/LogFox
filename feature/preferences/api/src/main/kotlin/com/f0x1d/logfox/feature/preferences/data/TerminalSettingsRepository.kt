package com.f0x1d.logfox.feature.preferences.data

import com.f0x1d.logfox.feature.terminals.base.TerminalType
import kotlinx.coroutines.flow.Flow

interface TerminalSettingsRepository {
    val TERMINAL_TYPE_DEFAULT: TerminalType get() = TerminalType.Default

    var selectedTerminalType: TerminalType
    val selectedTerminalTypeFlow: Flow<TerminalType>

    var fallbackToDefaultTerminal: Boolean

    fun selectTerminal(type: TerminalType)
}
