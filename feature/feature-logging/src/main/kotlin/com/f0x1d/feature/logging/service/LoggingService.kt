package com.f0x1d.feature.logging.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.f0x1d.logfox.arch.di.MainDispatcher
import com.f0x1d.logfox.context.LOGGING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.context.activityManager
import com.f0x1d.logfox.context.toast
import com.f0x1d.logfox.feature.crashes.core.repository.CrashesRepository
import com.f0x1d.logfox.feature.logging.core.repository.LoggingRepository
import com.f0x1d.logfox.feature.logging.core.store.LoggingStore
import com.f0x1d.logfox.feature.recordings.core.controller.RecordingController
import com.f0x1d.logfox.intents.EXIT_APP_INTENT_ID
import com.f0x1d.logfox.intents.makeServicePendingIntent
import com.f0x1d.logfox.model.exception.TerminalNotSupportedException
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.terminals.DefaultTerminal
import com.f0x1d.logfox.terminals.base.Terminal
import com.f0x1d.logfox.ui.Icons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.LinkedList
import javax.inject.Inject

@AndroidEntryPoint
class LoggingService : LifecycleService() {

    companion object {
        const val ACTION_RESTART_LOGGING = "logfox.RESTART_LOGGING"
        const val ACTION_CLEAR_LOGS = "logfox.CLEAR_LOGS"

        const val ACTION_KILL_SERVICE = "logfox.KILL_SERVICE"
    }

    private val binder = LocalBinder()

    @Inject
    lateinit var loggingRepository: LoggingRepository

    @Inject
    lateinit var crashesRepository: CrashesRepository

    @Inject
    lateinit var recordingController: RecordingController

    @Inject
    lateinit var loggingStore: LoggingStore

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var terminals: Array<Terminal>

    @MainDispatcher
    @Inject
    lateinit var mainDispatcher: CoroutineDispatcher

    private val logs = LinkedList<LogLine>()
    private val logsMutex = Mutex()
    private var loggingJob: Job? = null

    private var idsCounter = -1L

    override fun onCreate() {
        super.onCreate()

        startForeground(-1, notification())
        startLogging()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_RESTART_LOGGING -> restartLogging()
            ACTION_CLEAR_LOGS -> clearLogs()

            ACTION_KILL_SERVICE -> killApp()
        }

        return START_NOT_STICKY
    }

    private fun startLogging() {
        if (loggingJob?.isActive == true) return

        var loggingTerminal = terminals[appPreferences.selectedTerminalIndex]

        loggingJob = lifecycleScope.launch {
            try {
                while (true) {
                    loggingRepository.startLogging(
                        terminal = loggingTerminal,
                        startingId = idsCounter,
                    ).catch { throwable ->
                        if (throwable is TerminalNotSupportedException) {
                            if (appPreferences.fallbackToDefaultTerminal) withContext(mainDispatcher) {
                                toast(Strings.terminal_unavailable_falling_back)

                                loggingTerminal.exit()
                                loggingTerminal = terminals[DefaultTerminal.INDEX]
                            } else {
                                delay(10000) // waiting for 10sec before new attempt
                            }
                        } else {
                            throwable.printStackTrace()
                        }
                    }.collect { logLine ->
                        logsMutex.withLock {
                            logs.add(logLine)

                            while (logs.size > appPreferences.logsDisplayLimit)
                                logs.removeFirst()
                        }

                        loggingStore.updateLogs(logs)

                        crashesRepository.readers.forEach {
                            it(logLine)
                        }
                        recordingController.reader(logLine)
                    }
                }
            } finally {
                withContext(NonCancellable) {
                    recordingController.loggingStopped()
                    clearLogs().join()

                    loggingTerminal.exit()
                }
            }
        }
    }

    private fun restartLogging() = lifecycleScope.launch {
        loggingJob?.cancelAndJoin()
        startLogging()
    }

    private fun clearLogs() = lifecycleScope.launch {
        logsMutex.withLock {
            logs.clear()
        }

        loggingStore.updateLogs(emptyList())
    }

    private fun notification() = NotificationCompat.Builder(this, LOGGING_STATUS_CHANNEL_ID)
        .setContentTitle(getString(Strings.logging))
        .setSmallIcon(Icons.ic_logfox)
        .setOngoing(true)
        .addAction(
            Icons.ic_clear,
            getString(Strings.exit),
            makeServicePendingIntent(EXIT_APP_INTENT_ID, LoggingService::class.java) {
                action = ACTION_KILL_SERVICE
            }
        )
        .build()

    private fun killApp() = lifecycleScope.launch {
        loggingJob?.cancelAndJoin()
        clearLogs().join()

        activityManager.appTasks.forEach {
            it.finishAndRemoveTask()
        }

        ServiceCompat.stopForeground(this@LoggingService, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    inner class LocalBinder : Binder() {
        val service: LoggingService get() = this@LoggingService
    }
}
