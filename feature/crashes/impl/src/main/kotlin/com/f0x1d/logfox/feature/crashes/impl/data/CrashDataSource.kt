package com.f0x1d.logfox.feature.crashes.impl.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine

internal interface CrashDataSource {
    suspend fun process(line: LogLine)
}
