package com.f0x1d.logfox.feature.terminals.domain

import com.f0x1d.logfox.feature.terminals.base.Terminal
import javax.inject.Inject

internal class ExitTerminalUseCaseImpl @Inject constructor() : ExitTerminalUseCase {
    override suspend fun invoke(terminal: Terminal) {
        terminal.exit()
    }
}
