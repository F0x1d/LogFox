package com.f0x1d.logfox.logging.readers.base

import com.f0x1d.logfox.logging.model.LogLine

interface BaseReader {
    suspend fun readLine(line: LogLine)
}