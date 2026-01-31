package com.f0x1d.logfox.feature.terminals.api.domain

import com.f0x1d.logfox.feature.terminals.api.base.Terminal

interface GetSelectedTerminalUseCase {
    operator fun invoke(): Terminal
}
