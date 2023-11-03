package com.f0x1d.logfox.repository.logging

import android.content.Context
import android.content.SharedPreferences
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.context.toast
import com.f0x1d.logfox.extensions.logline.LogLine
import com.f0x1d.logfox.extensions.updateList
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.base.BaseRepository
import com.f0x1d.logfox.repository.logging.base.LoggingHelperRepository
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.utils.terminal.DefaultTerminal
import com.f0x1d.logfox.utils.terminal.base.Terminal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences,
    private val terminals: Array<Terminal>,
    private val helpers: Array<LoggingHelperRepository>
): BaseRepository(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private val COMMAND = arrayOf("logcat" , "-v", "uid", "-v", "epoch", "-T", "1")
    }

    val logsFlow = MutableStateFlow(emptyList<LogLine>())
    val serviceRunningFlow = MutableStateFlow(false)

    private var loggingJob: Job? = null

    private var loggingTerminal = terminals.first()
    private var loggingInterval = AppPreferences.LOGS_UPDATE_INTERVAL_DEFAULT
    private var logsDisplayLimit = AppPreferences.LOGS_DISPLAY_LIMIT_DEFAULT

    private var idsCounter = -1L

    private val packageManager = context.packageManager

    fun startLoggingIfNot() {
        if (loggingJob?.isActive == true) return

        loggingTerminal = terminals[appPreferences.selectedTerminalIndex]
        loggingInterval = appPreferences.logsUpdateInterval
        logsDisplayLimit = appPreferences.logsDisplayLimit
        appPreferences.registerListener(this)

        loggingJob = onAppScope {
            helpers.map {
                async(Dispatchers.IO) { it.setup() }
            }.awaitAll()

            while (isActive) {
                readLogs()
            }
        }
    }

    fun restartLogging(updateTerminal: Boolean = true) {
        loggingJob?.cancel()

        if (updateTerminal) {
            val prevTerminal = loggingTerminal
            loggingTerminal = terminals[appPreferences.selectedTerminalIndex]

            if (prevTerminal != loggingTerminal) onAppScope {
                prevTerminal.exit()
            }
        }

        loggingJob = onAppScope {
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
            val closingHelpers = helpers.map {
                async(Dispatchers.IO) { it.stop() }
            }

            loggingTerminal.exit()
            closingHelpers.awaitAll()
        }
    }

    fun clearLogs() = runOnAppScope {
        logsFlow.update {
            emptyList()
        }
    }

    private suspend fun fallbackToDefaultTerminal() {
        if (appPreferences.fallbackToDefaultTerminal) withContext(Dispatchers.Main) {
            context.toast(R.string.terminal_unavailable_falling_back)

            loggingTerminal = terminals[DefaultTerminal.INDEX]
            restartLogging(updateTerminal = false)
        } else
            delay(10000) // waiting for 10sec before new attempt
    }

    private suspend fun readLogs() = coroutineScope {
        if (!loggingTerminal.isSupported()) {
            fallbackToDefaultTerminal()
            return@coroutineScope
        }

        val process = loggingTerminal.execute(*COMMAND)
        if (process == null) {
            fallbackToDefaultTerminal()
            return@coroutineScope
        }

        val updateLines = mutableListOf<LogLine>()
        val mutex = Mutex()

        val updater = launch(Dispatchers.IO) {
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

        try {
            process.output.bufferedReader().useLines {
                var droppedFirst = false
                // avoiding getting the same line after logging restart because of
                // WARNING: -T 0 invalid, setting to 1
                for (line in it) {
                    if (!isActive) break

                    val logLine = LogLine(idsCounter++, line, context) ?: continue
                    if (!droppedFirst) {
                        droppedFirst = true
                        continue
                    }

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

            process.destroy()
        } catch (e: Exception) {
            // closed or dead
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