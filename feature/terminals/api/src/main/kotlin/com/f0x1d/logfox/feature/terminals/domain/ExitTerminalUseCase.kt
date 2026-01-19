package com.f0x1d.logfox.feature.terminals.domain

import com.f0x1d.logfox.feature.terminals.base.Terminal

interface ExitTerminalUseCase {
    suspend operator fun invoke(terminal: Terminal)
}
