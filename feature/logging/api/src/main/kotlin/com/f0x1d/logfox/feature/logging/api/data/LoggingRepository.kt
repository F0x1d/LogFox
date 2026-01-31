package com.f0x1d.logfox.feature.logging.api.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.terminals.api.base.Terminal
import kotlinx.coroutines.flow.Flow

interface LoggingRepository {

    companion object {
        val COMMAND = arrayOf("logcat", "-v", "uid", "-v", "epoch")

        val DUMP_FLAG = arrayOf("-d")
    }

    fun startLogging(
        terminal: Terminal,
        startingId: Long = 0,
        startLogsTime: Long? = null,
    ): Flow<LogLine>

    fun dumpLogs(terminal: Terminal): Flow<LogLine>
}
