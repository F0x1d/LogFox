package com.f0x1d.logfox.feature.preferences.impl.data.terminal

import com.fredporciuncula.flow.preferences.Preference

internal interface TerminalSettingsLocalDataSource {
    fun selectedTerminalTypeKey(): Preference<String>
    fun fallbackToDefaultTerminal(): Preference<Boolean>
}
