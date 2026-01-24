package com.f0x1d.logfox.feature.preferences.domain.terminal

import com.f0x1d.logfox.feature.terminals.base.TerminalType

interface SetSelectedTerminalTypeUseCase {
    operator fun invoke(type: TerminalType)
}
