package com.f0x1d.logfox.feature.logging.api.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine

interface LogLineFormatterRepository {
    fun formatForExport(logLine: LogLine): String

    fun formatOriginal(logLine: LogLine): String
}
