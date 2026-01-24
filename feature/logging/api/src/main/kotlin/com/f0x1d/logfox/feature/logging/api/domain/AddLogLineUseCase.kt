package com.f0x1d.logfox.feature.logging.api.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine

interface AddLogLineUseCase {
    suspend operator fun invoke(logLine: LogLine)
}
