package com.f0x1d.logfox.feature.logging.impl.data

import android.content.Context
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.feature.logging.api.data.LoggingRepository
import com.f0x1d.logfox.model.exception.TerminalNotSupportedException
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.terminals.base.Terminal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

internal class LoggingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : LoggingRepository {

    override fun startLogging(
        terminal: Terminal,
        startingId: Long,
    ): Flow<LogLine> = channelFlow {
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

    override fun dumpLogs(terminal: Terminal): Flow<LogLine> = channelFlow {
        val command = LoggingRepository.COMMAND + LoggingRepository.DUMP_FLAG

        emitLines(
            terminal = terminal,
            command = command,
            startingId = 0,
        )
    }.flowOn(ioDispatcher)

    private suspend fun ProducerScope<LogLine>.emitLines(
        terminal: Terminal,
        command: Array<String>,
        startingId: Long = 0,
    ) {
        if (terminal.isSupported().not()) {
            Timber.d("terminal $terminal is not supported")
            throw TerminalNotSupportedException()
        }

        val process = terminal.execute(*command) ?: throw TerminalNotSupportedException()
        Timber.d("started process")

        var idsCounter = startingId
        Timber.d("starting with id $idsCounter")

        try {
            val readerScope = CoroutineScope(ioDispatcher + SupervisorJob())
            invokeOnClose { readerScope.cancel() }

            process.output.bufferedReader().useLines { linesSequence ->
                var droppedFirst = !appPreferences.showLogsFromAppLaunch
                // avoiding getting the same line after logging restart because of
                // WARNING: -T 0 invalid, setting to 1
                val iterator = linesSequence.iterator()

                var looping = true
                while (looping) {
                    withTimeout(10.seconds) {
                        readerScope.launch {
                            if (iterator.hasNext().not()) {
                                looping = false
                            } else {
                                val line = iterator.next()
                                Timber.d("got line $line")

                                val logLine = LogLine(
                                    id = idsCounter++,
                                    line = line,
                                    context = context,
                                )
                                Timber.d("successfully parsed $line to $logLine")

                                if (droppedFirst.not()) {
                                    droppedFirst = true
                                } else {
                                    logLine?.let { send(it) }
                                }
                            }
                        }.join()
                    }
                }
            }
        } finally {
            Timber.d("destroying process")
            runCatching {
                process.destroy()
            }
        }
    }
}
