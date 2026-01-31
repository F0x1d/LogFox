package com.f0x1d.logfox.feature.terminals.api.domain

import com.f0x1d.logfox.feature.terminals.api.base.Terminal

interface ExitTerminalUseCase {
    suspend operator fun invoke(terminal: Terminal)
}
