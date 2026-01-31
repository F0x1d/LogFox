package com.f0x1d.logfox.feature.preferences.api.data

import com.f0x1d.logfox.core.preferences.api.PreferenceStateFlow
import com.f0x1d.logfox.feature.terminals.api.base.TerminalType

interface TerminalSettingsRepository {
    fun selectedTerminalType(): PreferenceStateFlow<TerminalType>
    fun fallbackToDefaultTerminal(): PreferenceStateFlow<Boolean>
}
