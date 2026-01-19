package com.f0x1d.logfox.feature.preferences.domain

import com.f0x1d.logfox.feature.terminals.base.TerminalType

interface GetSelectedTerminalTypeUseCase {
    operator fun invoke(): TerminalType
}
