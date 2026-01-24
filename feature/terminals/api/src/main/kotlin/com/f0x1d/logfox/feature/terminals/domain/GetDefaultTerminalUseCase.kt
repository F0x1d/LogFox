package com.f0x1d.logfox.feature.terminals.domain

import com.f0x1d.logfox.feature.terminals.base.Terminal

interface GetDefaultTerminalUseCase {
    operator fun invoke(): Terminal
}
