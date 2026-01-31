package com.f0x1d.logfox.feature.preferences.api.domain.terminal

import com.f0x1d.logfox.feature.terminals.api.base.TerminalType
import kotlinx.coroutines.flow.Flow

interface GetSelectedTerminalTypeFlowUseCase {
    operator fun invoke(): Flow<TerminalType>
}
