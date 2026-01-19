package com.f0x1d.logfox.feature.setup.api.domain

import com.f0x1d.logfox.feature.terminals.base.TerminalType

interface SelectTerminalUseCase {
    suspend operator fun invoke(type: TerminalType)
}
