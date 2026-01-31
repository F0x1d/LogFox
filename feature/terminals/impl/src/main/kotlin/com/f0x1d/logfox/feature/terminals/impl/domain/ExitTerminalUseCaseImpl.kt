package com.f0x1d.logfox.feature.terminals.impl.domain

import com.f0x1d.logfox.feature.terminals.api.base.Terminal
import com.f0x1d.logfox.feature.terminals.api.domain.ExitTerminalUseCase
import javax.inject.Inject

internal class ExitTerminalUseCaseImpl @Inject constructor() : ExitTerminalUseCase {
    override suspend fun invoke(terminal: Terminal) {
        terminal.exit()
    }
}
