package com.f0x1d.logfox.feature.crashes.api.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine

interface CrashDetectingRepository {
    suspend fun processLogLine(line: LogLine)
}
