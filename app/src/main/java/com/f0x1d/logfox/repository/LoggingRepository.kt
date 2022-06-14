package com.f0x1d.logfox.repository

import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.extensions.LogLine
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.base.BaseRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggingRepository @Inject constructor(crashesRepository: CrashesRepository): BaseRepository() {

    companion object {
        const val LOGS_LIMIT = 10000
        const val LOGS_INTERVAL = 150L
    }

    val logsFlow = MutableStateFlow(emptyList<LogLine>())

    private var loggingJob: Job? = null
    private var idsCounter = -1L

    private val helpers = listOf(crashesRepository)

    fun startLoggingIfNot() {
        if (loggingJob?.isActive == true) return

        loggingJob = LogFoxApp.applicationScope.launch(Dispatchers.Default) {
            helpers.forEach {
                it.setup()
            }

            while (isActive) {
                readLogs()
            }
        }
    }

    fun stopLogging() {
        loggingJob?.cancel()

        clearLogs()

        helpers.forEach { it.stop() }
    }

    fun clearLogs() {
        logsFlow.update { emptyList() }
    }

    private suspend fun readLogs() = coroutineScope {
        val stream = run {
            Runtime.getRuntime().exec("logcat -c").waitFor()
            Runtime.getRuntime().exec("logcat -v epoch").inputStream
        }

        val updateLines = mutableListOf<LogLine>()
        val mutex = Mutex()

        val updater = launch {
            while (isActive) {
                delay(LOGS_INTERVAL)

                logsFlow.update {
                    it.toMutableList().apply {
                        mutex.withLock {
                            addAll(updateLines)
                            updateLines.clear()
                        }

                        while (size > LOGS_LIMIT) {
                            removeAt(0)
                        }
                    }
                }
            }
        }

        val reader = BufferedReader(InputStreamReader(stream))
        var line: String?
        while (isActive) {
            line = reader.readLine()
            if (line == null) break

            val logLine = LogLine(idsCounter++, line) ?: continue

            mutex.withLock {
                updateLines.add(logLine)
            }

            helpers.forEach { helper ->
                helper.readers.forEach {
                    it.readLine(logLine)
                }
            }
        }

        reader.close()
        updater.cancel()
    }
}