package com.f0x1d.logfox.feature.logging.impl.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine

internal interface LogsBufferDataSource {
    suspend fun add(logLine: LogLine, limit: Int)
    suspend fun getAll(): List<LogLine>
    suspend fun clear()
    suspend fun lastLog(): LogLine?
}
