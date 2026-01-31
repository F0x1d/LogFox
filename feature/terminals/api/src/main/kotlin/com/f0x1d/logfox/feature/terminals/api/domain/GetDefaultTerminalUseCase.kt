package com.f0x1d.logfox.feature.terminals.api.domain

import com.f0x1d.logfox.feature.terminals.api.base.Terminal

interface GetDefaultTerminalUseCase {
    operator fun invoke(): Terminal
}
