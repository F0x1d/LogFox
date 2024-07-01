package com.f0x1d.logfox.feature.logging.core.repository

import android.content.Context
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.repository.BaseRepository
import com.f0x1d.logfox.model.exception.TerminalNotSupportedException
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.terminals.base.Terminal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface LoggingRepository {

    companion object {
        val COMMAND = arrayOf("logcat" , "-v", "uid", "-v", "epoch")

        val DUMP_FLAG = arrayOf("-d")
        val SHOW_LOGS_FROM_NOW_FLAGS = arrayOf("-T", "1")
    }

    fun startLogging(
        terminal: Terminal,
        startingId: Long = 0,
    ): Flow<LogLine>

    fun dumpLogs(terminal: Terminal): Flow<LogLine>
}

internal class LoggingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
): BaseRepository(), LoggingRepository {

    override fun startLogging(
        terminal: Terminal,
        startingId: Long,
    ): Flow<LogLine> = flow {
        val command = LoggingRepository.COMMAND + when (appPreferences.showLogsFromAppLaunch) {
            true -> LoggingRepository.SHOW_LOGS_FROM_NOW_FLAGS

            else -> emptyArray()
        }

        emitLines(
            terminal = terminal,
            command = command,
            startingId = startingId,
        )
    }.flowOn(ioDispatcher)

    override fun dumpLogs(terminal: Terminal): Flow<LogLine> = flow {
        val command = LoggingRepository.COMMAND + LoggingRepository.DUMP_FLAG

        emitLines(
            terminal = terminal,
            command = command,
            startingId = 0,
        )
    }.flowOn(ioDispatcher)

    private suspend fun FlowCollector<LogLine>.emitLines(
        terminal: Terminal,
        command: Array<String>,
        startingId: Long = 0,
    ) {
        if (terminal.isSupported().not()) {
            throw TerminalNotSupportedException()
        }

        val process = terminal.execute(*command) ?: throw TerminalNotSupportedException()

        var idsCounter = startingId

        try {
            process.output.bufferedReader().useLines {
                var droppedFirst = !appPreferences.showLogsFromAppLaunch
                // avoiding getting the same line after logging restart because of
                // WARNING: -T 0 invalid, setting to 1
                for (line in it) {
                    val logLine = LogLine(idsCounter++, line, context) ?: continue
                    if (!droppedFirst) {
                        droppedFirst = true
                        continue
                    }

                    emit(logLine)
                }
            }
        } finally {
            runCatching {
                process.destroy()
            }
        }
    }
}
