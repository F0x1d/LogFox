package com.f0x1d.logfox.feature.logging.impl.data

import android.content.Context
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.logging.api.data.LoggingRepository
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.preferences.data.ServiceSettingsRepository
import com.f0x1d.logfox.feature.terminals.base.Terminal
import com.f0x1d.logfox.feature.terminals.exception.TerminalNotSupportedException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import java.io.BufferedReader
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import com.f0x1d.logfox.feature.logging.api.model.LogLine as LogLineFactory

internal class LoggingRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val serviceSettingsRepository: ServiceSettingsRepository,
        @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : LoggingRepository {
        override fun startLogging(
            terminal: Terminal,
            startingId: Long,
        ): Flow<LogLine> =
            flow {
                val command =
                    LoggingRepository.COMMAND +
                        when (serviceSettingsRepository.showLogsFromAppLaunch().value) {
                            true -> LoggingRepository.SHOW_LOGS_FROM_NOW_FLAGS
                            else -> emptyArray()
                        }

                emitLines(
                    terminal = terminal,
                    command = command,
                    startingId = startingId,
                )
            }.flowOn(ioDispatcher)

        override fun dumpLogs(terminal: Terminal): Flow<LogLine> =
            flow {
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
                Timber.d("terminal $terminal is not supported")
                throw TerminalNotSupportedException()
            }

            val process = terminal.execute(*command) ?: throw TerminalNotSupportedException()
            Timber.d("started process")

            var idsCounter = startingId
            Timber.d("starting with id $idsCounter")

            try {
                Timber.d("started scope")

                process.output.bufferedReader().use { reader ->
                    var droppedFirst = !serviceSettingsRepository.showLogsFromAppLaunch().value
                    // avoiding getting the same line after logging restart because of
                    // WARNING: -T 0 invalid, setting to 1
                    Timber.d("got reader")

                    while (true) {
                        withTimeout(10.seconds) {
                            Timber.d("started awaiting line")
                            val line = reader.readLineCancellable()
                            Timber.d("got line $line")

                            val logLine =
                                LogLineFactory(
                                    id = idsCounter++,
                                    line = line,
                                    context = context,
                                )
                            Timber.d("successfully parsed $line to $logLine")

                            if (droppedFirst.not()) {
                                droppedFirst = true
                            } else {
                                logLine?.let { emit(it) }
                            }
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

        private suspend fun BufferedReader.readLineCancellable(): String =
            withContext(ioDispatcher) {
                while (true) {
                    if (ready()) {
                        return@withContext readLine()
                    }

                    delay(100L)
                }

                "not reachable"
            }
    }
