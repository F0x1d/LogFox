package com.f0x1d.logfox.feature.logging.api.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine

interface LogLineFormatterRepository {
    fun format(
        logLine: LogLine,
        formatDate: (Long) -> String,
        formatTime: (Long) -> String,
    ): String
}
