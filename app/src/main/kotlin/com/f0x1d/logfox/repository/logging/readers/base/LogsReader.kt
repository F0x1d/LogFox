package com.f0x1d.logfox.repository.logging.readers.base

import com.f0x1d.logfox.model.logline.LogLine

interface LogsReader {
    suspend fun readLine(line: LogLine)
}
