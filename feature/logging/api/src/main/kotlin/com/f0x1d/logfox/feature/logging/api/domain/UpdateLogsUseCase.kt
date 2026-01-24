package com.f0x1d.logfox.feature.logging.api.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine

interface UpdateLogsUseCase {
    suspend operator fun invoke(logs: List<LogLine>)
}
