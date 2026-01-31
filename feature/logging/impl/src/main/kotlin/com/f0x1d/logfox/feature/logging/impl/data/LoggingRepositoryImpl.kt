package com.f0x1d.logfox.feature.logging.impl.data

import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.logging.api.data.LogLineParser
import com.f0x1d.logfox.feature.logging.api.data.LoggingRepository
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.terminals.api.base.Terminal
import com.f0x1d.logfox.feature.terminals.api.exception.TerminalNotSupportedException
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

internal class LoggingRepositoryImpl @Inject constructor(
    private val logLineParser: LogLineParser,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : LoggingRepository {

    override fun startLogging(
        terminal: Terminal,
        startingId: Long,
        startLogsTime: Long?,
    ): Flow<LogLine> = flow {
        val formattedTimestamp = startLogsTime?.let { formatTimestampForLogcat(it) }

        val command = LoggingRepository.COMMAND + if (formattedTimestamp == null) {
            emptyArray()
        } else {
            arrayOf("-T", formattedTimestamp)
        }
        Timber.d("Starting logging with command: ${command.joinToString(" ")}")

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
        startingId: Long,
    ) {
        if (terminal.isSupported().not()) {
            Timber.d("Terminal $terminal is not supported")
            throw TerminalNotSupportedException()
        }

        val process = terminal.execute(*command) ?: throw TerminalNotSupportedException()
        Timber.d("Started process")

        var idsCounter = startingId
        Timber.d("Starting with id $idsCounter")

        try {
            Timber.d("Started scope")

            process.output.bufferedReader().use { reader ->
                Timber.d("Got reader")

                while (true) {
                    Timber.d("Started awaiting line")
                    val line = withTimeout(10.seconds) {
                        reader.readLineCancellable()
                    }
                    Timber.d("Got line $line")

                    val logLine = logLineParser.parse(
                        id = idsCounter++,
                        line = line,
                    )
                    Timber.d("Parsed $line to $logLine")

                    if (logLine == null) continue

                    emit(logLine)
                }
            }
        } finally {
            Timber.d("Destroying process")
            runCatching {
                process.destroy()
            }
        }
    }

    private suspend fun BufferedReader.readLineCancellable(): String = withContext(ioDispatcher) {
        while (true) {
            if (ready()) {
                return@withContext readLine()
            }
            delay(100L)
        }

        "not reachable"
    }

    private fun formatTimestampForLogcat(timeMillis: Long): String {
        val seconds = timeMillis / 1000
        val millis = timeMillis % 1000
        return "$seconds.$millis"
    }
}
