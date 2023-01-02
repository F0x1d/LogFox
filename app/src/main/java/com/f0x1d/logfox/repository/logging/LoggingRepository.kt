package com.f0x1d.logfox.repository.logging

import android.content.SharedPreferences
import com.f0x1d.logfox.extensions.logline.LogLine
import com.f0x1d.logfox.extensions.updateList
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.base.BaseRepository
import com.f0x1d.logfox.utils.preferences.AppPreferences
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
class LoggingRepository @Inject constructor(
    crashesRepository: CrashesRepository,
    recordingsRepository: RecordingsRepository,
    filtersRepository: FiltersRepository,
    private val appPreferences: AppPreferences
): BaseRepository(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val LOGS_LIMIT = 10000
    }

    val logsFlow = MutableStateFlow(emptyList<LogLine>())
    val serviceRunningFlow = MutableStateFlow(false)

    private var loggingJob: Job? = null
    private var loggingInterval = AppPreferences.LOGS_UPDATE_INTERVAL_DEFAULT
    private var idsCounter = -1L

    private val helpers = listOf(
        crashesRepository,
        recordingsRepository,
        filtersRepository
    )

    fun startLoggingIfNot() {
        if (loggingJob?.isActive == true) return

        loggingInterval = appPreferences.logsUpdateInterval
        appPreferences.registerListener(this)

        loggingJob = onAppScope {
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
        appPreferences.unregisterListener(this)

        onAppScope {
            helpers.forEach {
                it.stop()
            }
        }
    }

    fun clearLogs() {
        logsFlow.update {
            emptyList()
        }
    }

    private suspend fun readLogs() = coroutineScope {
        val stream = Runtime.getRuntime().exec("logcat -v epoch -T 0").inputStream

        val updateLines = mutableListOf<LogLine>()
        val mutex = Mutex()

        val updater = launch {
            while (isActive) {
                delay(loggingInterval)

                logsFlow.updateList {
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "pref_logs_update_interval") {
            loggingInterval = appPreferences.logsUpdateInterval
        }
    }
}