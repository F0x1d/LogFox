package com.f0x1d.logfox.repository.logging.readers.base

interface LogsReader {
    suspend fun readLine(line: com.f0x1d.logfox.model.LogLine)
}
