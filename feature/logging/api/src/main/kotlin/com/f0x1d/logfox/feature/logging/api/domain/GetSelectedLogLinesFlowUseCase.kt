package com.f0x1d.logfox.feature.logging.api.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import kotlinx.coroutines.flow.Flow

interface GetSelectedLogLinesFlowUseCase {
    operator fun invoke(): Flow<List<LogLine>>
}
