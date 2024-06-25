package com.f0x1d.logfox.repository.logging

import android.content.Context
import android.content.SharedPreferences
import com.f0x1d.logfox.R
import com.f0x1d.logfox.context.toast
import com.f0x1d.logfox.extensions.onAppScope
import com.f0x1d.logfox.extensions.runOnAppScope
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.repository.base.BaseRepository
import com.f0x1d.logfox.repository.logging.base.LoggingHelperRepository
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
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggingRepository2 @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences,
    private val terminals: Array<com.f0x1d.logfox.terminals.base.Terminal>,
    private val helpers: Array<LoggingHelperRepository>
): BaseRepository(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        val COMMAND = arrayOf("logcat" , "-v", "uid", "-v", "epoch")

        val DUMP_FLAG = arrayOf("-d")
        val SHOW_LOGS_FROM_NOW_FLAGS = arrayOf("-T", "1")
    }

    val logsFlow = MutableStateFlow(emptyList<LogLine>())
    val serviceRunningFlow = MutableStateFlow(false)

    private val logs = LinkedList<LogLine>()
    private val logsMutex = Mutex()
    private var loggingJob: Job? = null

    private var loggingTerminal = terminals[appPreferences.selectedTerminalIndex]
    private var loggingInterval = AppPreferences.LOGS_UPDATE_INTERVAL_DEFAULT
    private var logsDisplayLimit = AppPreferences.LOGS_DISPLAY_LIMIT_DEFAULT

    private var idsCounter = -1L

    fun startLoggingIfNot(updateTerminal: Boolean = true) {
        if (loggingJob?.isActive == true) return

        if (updateTerminal) {
            val prevTerminal = loggingTerminal
            loggingTerminal = terminals[appPreferences.selectedTerminalIndex]

            if (prevTerminal != loggingTerminal) runOnAppScope {
                prevTerminal.exit()
            }
        }

        loggingInterval = appPreferences.logsUpdateInterval
        logsDisplayLimit = appPreferences.logsDisplayLimit
        appPreferences.registerListener(this)

        loggingJob = onAppScope {
            helpers.map {
                async(Dispatchers.IO) {
                    it.setup()
                }
            }.awaitAll()

            while (isActive) {
                readLogs()
            }
        }
    }

    fun restartLogging(updateTerminal: Boolean = true) = runOnAppScope {
        stopLogging()
        startLoggingIfNot(updateTerminal = updateTerminal)
    }

    suspend fun stopLogging() = coroutineScope {
        loggingJob?.cancel()

        clearLogs()
        appPreferences.unregisterListener(this@LoggingRepository2)

        val closingHelpers = helpers.map {
            async(Dispatchers.IO) {
                it.stop()
            }
        }

        loggingTerminal.exit()
        closingHelpers.awaitAll()
    }

    fun clearLogs() = runOnAppScope {
        logsMutex.withLock {
            logs.clear()
        }

        logsFlow.update {
            emptyList()
        }
    }

    private suspend fun fallbackToDefaultTerminal() {
        if (appPreferences.fallbackToDefaultTerminal) withContext(Dispatchers.Main) {
            context.toast(R.string.terminal_unavailable_falling_back)

            loggingTerminal = terminals[com.f0x1d.logfox.terminals.DefaultTerminal.INDEX]
            restartLogging(updateTerminal = false)
        } else
            delay(10000) // waiting for 10sec before new attempt
    }

    private suspend fun readLogs() = coroutineScope {
        if (!loggingTerminal.isSupported()) {
            fallbackToDefaultTerminal()
            return@coroutineScope
        }

        val command = COMMAND + when (appPreferences.showLogsFromAppLaunch) {
            true -> SHOW_LOGS_FROM_NOW_FLAGS

            else -> emptyArray()
        }

        val process = loggingTerminal.execute(*command)
        if (process == null) {
            fallbackToDefaultTerminal()
            return@coroutineScope
        }

        val updater = launch(Dispatchers.IO) {
            while (isActive) {
                delay(loggingInterval)

                logsFlow.update {
                    logsMutex.withLock {
                        logs.toList()
                    }
                }
            }
        }

        try {
            process.output.bufferedReader().useLines {
                var droppedFirst = !appPreferences.showLogsFromAppLaunch
                // avoiding getting the same line after logging restart because of
                // WARNING: -T 0 invalid, setting to 1
                for (line in it) {
                    if (!isActive) break

                    val logLine = LogLine(idsCounter++, line, context) ?: continue
                    if (!droppedFirst) {
                        droppedFirst = true
                        continue
                    }

                    logsMutex.withLock {
                        logs.add(logLine)

                        while (logs.size > logsDisplayLimit)
                            logs.removeFirst()
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

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {

    }
}
