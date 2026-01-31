package com.f0x1d.logfox.feature.preferences.api.domain.terminal

import com.f0x1d.logfox.feature.terminals.api.base.TerminalType

interface GetSelectedTerminalTypeUseCase {
    operator fun invoke(): TerminalType
}
