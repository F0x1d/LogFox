package com.f0x1d.logfox.feature.logging.api.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.terminals.base.Terminal
import kotlinx.coroutines.flow.Flow

interface LoggingRepository {

    companion object {
        val COMMAND = arrayOf("logcat", "-v", "uid", "-v", "epoch")

        val DUMP_FLAG = arrayOf("-d")
        val SHOW_LOGS_FROM_NOW_FLAGS = arrayOf("-T", "1")
    }

    fun startLogging(terminal: Terminal, startingId: Long = 0): Flow<LogLine>

    fun dumpLogs(terminal: Terminal): Flow<LogLine>
}
