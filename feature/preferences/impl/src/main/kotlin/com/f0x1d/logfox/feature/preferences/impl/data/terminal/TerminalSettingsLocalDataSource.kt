package com.f0x1d.logfox.feature.preferences.impl.data.terminal

import com.f0x1d.logfox.feature.terminals.base.TerminalType
import kotlinx.coroutines.flow.Flow

internal interface TerminalSettingsLocalDataSource {
    var selectedTerminalType: TerminalType
    val selectedTerminalTypeFlow: Flow<TerminalType>

    var fallbackToDefaultTerminal: Boolean

    fun selectTerminal(type: TerminalType)
}
