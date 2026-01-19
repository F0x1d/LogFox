package com.f0x1d.logfox.feature.preferences.data

import com.f0x1d.logfox.core.preferences.PreferenceStateFlow
import com.f0x1d.logfox.feature.terminals.base.TerminalType

interface TerminalSettingsRepository {
    fun selectedTerminalType(): PreferenceStateFlow<TerminalType>
    fun fallbackToDefaultTerminal(): PreferenceStateFlow<Boolean>
}
