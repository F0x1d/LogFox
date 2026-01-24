package com.f0x1d.logfox.feature.preferences.domain.terminal

import com.f0x1d.logfox.feature.terminals.base.TerminalType
import kotlinx.coroutines.flow.Flow

interface GetSelectedTerminalTypeFlowUseCase {
    operator fun invoke(): Flow<TerminalType>
}
