package com.f0x1d.logfox.feature.crashes.api.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine

interface ProcessLogLineCrashesUseCase {
    suspend operator fun invoke(logLine: LogLine)
}
