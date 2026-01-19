package com.f0x1d.logfox.feature.logging.api.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.terminals.base.Terminal
import kotlinx.coroutines.flow.Flow

interface StartLoggingUseCase {
    operator fun invoke(
        terminal: Terminal,
        startingId: Long = 0,
    ): Flow<LogLine>
}
