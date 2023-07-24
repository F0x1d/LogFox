package com.f0x1d.logfox.repository.logging

import android.content.SharedPreferences
import com.f0x1d.logfox.extensions.RootState
import com.f0x1d.logfox.extensions.haveRoot
import com.f0x1d.logfox.extensions.logline.LogLine
import com.f0x1d.logfox.extensions.updateList
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.base.BaseRepository
import com.f0x1d.logfox.utils.preferences.AppPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
        private const val COMMAND = "logcat -v epoch -T 0"
    }

    val logsFlow = MutableStateFlow(emptyList<LogLine>())
    val serviceRunningFlow = MutableStateFlow(false)
    val rootStateFlow = MutableStateFlow(RootState.UNKNOWN)

    private var loggingJob: Job? = null

    private var loggingInterval = AppPreferences.LOGS_UPDATE_INTERVAL_DEFAULT
    private var logsDisplayLimit = AppPreferences.LOGS_DISPLAY_LIMIT_DEFAULT

    private var idsCounter = -1L

    private val helpers = listOf(
        crashesRepository,
        recordingsRepository,
        filtersRepository
    )

    fun startLoggingIfNot() {
        if (loggingJob?.isActive == true) return

        loggingInterval = appPreferences.logsUpdateInterval
        logsDisplayLimit = appPreferences.logsDisplayLimit
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

    fun clearLogs() = runOnAppScope {
        logsFlow.update {
            emptyList()
        }
    }

    private suspend fun readLogs() = coroutineScope {
        val stream = haveRoot.let { haveRoot ->
            rootStateFlow.update { if (haveRoot) RootState.YES else RootState.NO }

            Runtime.getRuntime().exec("${if (haveRoot) "su -c " else ""}$COMMAND").inputStream
        }

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

                    while (size > logsDisplayLimit) {
                        removeFirst()
                    }
                }
            }
        }

        stream.bufferedReader().useLines {
            for (line in it) {
                if (!isActive) break

                val logLine = LogLine(idsCounter++, line) ?: continue

                mutex.withLock {
                    updateLines.add(logLine)
                }

                helpers.forEach { helper ->
                    helper.readers.forEach { reader ->
                        reader.readLine(logLine)
                    }
                }
            }
        }

        updater.cancel()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "pref_logs_update_interval" -> loggingInterval = appPreferences.logsUpdateInterval
            "pref_logs_display_limit" -> logsDisplayLimit = appPreferences.logsDisplayLimit
        }
    }
}