package com.f0x1d.logfox.feature.logging.core.repository.logging

import android.content.Context
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.repository.BaseRepository
import com.f0x1d.logfox.model.exception.TerminalNotSupportedException
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.terminals.base.Terminal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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
        showLogsFromAppLaunch: Boolean,
        startingId: Long = 0,
    ): Flow<LogLine>
}

internal class LoggingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
): BaseRepository(), LoggingRepository {

    override fun startLogging(
        terminal: Terminal,
        showLogsFromAppLaunch: Boolean,
        startingId: Long,
    ) = flow {
        if (terminal.isSupported().not()) {
            throw TerminalNotSupportedException()
        }

        val command = LoggingRepository.COMMAND + when (showLogsFromAppLaunch) {
            true -> LoggingRepository.SHOW_LOGS_FROM_NOW_FLAGS

            else -> emptyArray()
        }

        val process = terminal.execute(*command) ?: throw TerminalNotSupportedException()

        var idsCounter = startingId

        process.output.bufferedReader().useLines {
            var droppedFirst = !showLogsFromAppLaunch
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

        runCatching {
            process.destroy()
        }
    }.flowOn(ioDispatcher)
}
