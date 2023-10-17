package com.f0x1d.logfox.repository.logging.readers.crashes

import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.readers.base.LogsReader
import com.f0x1d.logfox.utils.LimitedArrayList
import com.f0x1d.logfox.utils.preferences.AppPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DumpCollector @Inject constructor(
    appPreferences: AppPreferences
): LogsReader {

    var capacity = appPreferences.logsDumpLinesCount
        set(value) {
            logsDump.capacity = value
            field = value
        }

    val logsDump = LimitedArrayList<LogLine>(capacity)

    fun dump() = logsDump.joinToString("\n") {
        it.original
    }

    override suspend fun readLine(line: LogLine) {
        logsDump.add(line)
    }
}