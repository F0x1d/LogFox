package com.f0x1d.logfox.logging

import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.extensions.LogLine
import com.f0x1d.logfox.extensions.sendErrorNotification
import com.f0x1d.logfox.logging.model.LogLine
import com.f0x1d.logfox.logging.readers.base.BaseReader
import com.f0x1d.logfox.logging.readers.detectors.ANRDetector
import com.f0x1d.logfox.logging.readers.detectors.JNICrashDetector
import com.f0x1d.logfox.logging.readers.detectors.JavaCrashDetector
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.BufferedReader
import java.io.InputStreamReader

object Logging {

    const val LOGS_LIMIT = 10000
    const val LOGS_INTERVAL = 200L

    val logsFlow = MutableStateFlow(emptyList<LogLine>())
    val crashesFlow = MutableStateFlow(emptyList<AppCrash>())

    private var loggingJob: Job? = null
    private lateinit var database: AppDatabase
    private var inited = false
    private var idsCounter = -1L

    private val crashCollected: suspend (AppCrash) -> Unit = { appCrash ->
        crashesFlow.update {
            database.appCrashDao().insert(appCrash)

            it.toMutableList().apply {
                add(0, appCrash)
            }
        }

        LogFoxApp.instance.sendErrorNotification(appCrash)
    }
    private val additionalReaders = listOf<BaseReader>(
        JavaCrashDetector(crashCollected),
        JNICrashDetector(crashCollected),
        ANRDetector(crashCollected)
    )

    fun startLoggingIfNot() {
        if (loggingJob?.isActive == true) return

        loggingJob = LogFoxApp.applicationScope.launch(Dispatchers.Default) {
            if (!inited) init()

            while (isActive) {
                readLogs()
            }
        }
    }

    fun stopLogging() {
        loggingJob?.cancel()

        clearLogs()
    }

    fun clearLogs() {
        logsFlow.update { emptyList() }
    }

    fun clearCrashes() {
        LogFoxApp.applicationScope.launch(Dispatchers.Default) {
            crashesFlow.update {
                database.appCrashDao().deleteAll()
                emptyList()
            }
        }
    }

    private fun init() {
        database = EntryPointAccessors.fromApplication(LogFoxApp.instance, LoggingEntryPoint::class.java).appDatabase()

        crashesFlow.update {
            database.appCrashDao().getAll()
        }

        inited = true
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
            additionalReaders.forEach {
                it.readLine(logLine)
            }
        }

        reader.close()
        updater.cancel()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface LoggingEntryPoint {
        fun appDatabase(): AppDatabase
    }
}