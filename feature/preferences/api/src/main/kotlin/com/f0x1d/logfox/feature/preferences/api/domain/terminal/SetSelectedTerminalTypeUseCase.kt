package com.f0x1d.logfox.feature.preferences.api.domain.terminal

import com.f0x1d.logfox.feature.terminals.api.base.TerminalType

interface SetSelectedTerminalTypeUseCase {
    operator fun invoke(type: TerminalType)
}
