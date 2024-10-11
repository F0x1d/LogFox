package com.f0x1d.feature.logging.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.f0x1d.logfox.arch.EXIT_APP_INTENT_ID
import com.f0x1d.logfox.arch.LOGGING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.arch.OPEN_APP_INTENT_ID
import com.f0x1d.logfox.arch.activityManager
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.makeServicePendingIntent
import com.f0x1d.logfox.arch.toast
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.feature.crashes.core.controller.CrashesController
import com.f0x1d.logfox.feature.filters.core.repository.FiltersRepository
import com.f0x1d.logfox.feature.logging.core.model.suits
import com.f0x1d.logfox.feature.logging.core.repository.LoggingRepository
import com.f0x1d.logfox.feature.logging.core.store.LoggingStore
import com.f0x1d.logfox.feature.recordings.core.controller.RecordingController
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
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
    lateinit var crashesController: CrashesController

    @Inject
    lateinit var recordingController: RecordingController

    @Inject
    lateinit var filtersRepository: FiltersRepository

    @Inject
    lateinit var loggingStore: LoggingStore

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var terminals: Array<Terminal>

    @Inject
    lateinit var mainActivityPendingIntentProvider: MainActivityPendingIntentProvider

    @Inject
    @DefaultDispatcher
    lateinit var defaultDispatcher: CoroutineDispatcher

    private val logs = LinkedList<LogLine>()
    private val logsMutex = Mutex()
    private var loggingJob: Job? = null

    private lateinit var filtersState: StateFlow<List<UserFilter>>

    override fun onCreate() {
        super.onCreate()

        startForeground(-1, notification())
        startLogging()

        filtersState = filtersRepository
            .getAllEnabledAsFlow()
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Timber.d("got command ${intent?.action}")
        when (intent?.action) {
            ACTION_RESTART_LOGGING -> restartLogging()
            ACTION_CLEAR_LOGS -> clearLogs()

            ACTION_KILL_SERVICE -> killApp()
        }

        return START_NOT_STICKY
    }

    private fun startLogging() {
        Timber.d("startLogging")
        if (loggingJob?.isActive == true) return

        var loggingTerminal = terminals[appPreferences.selectedTerminalIndex]

        Timber.d("selected terminal $loggingTerminal")

        loggingJob = lifecycleScope.launch {
            try {
                launch {
                    while (true) {
                        delay(appPreferences.logsUpdateInterval)

                        logsMutex.withLock {
                            Timber.d("sending update logs to store")
                            loggingStore.updateLogs(logs)
                        }
                    }
                }

                while (true) {
                    Timber.d("in loop starting")

                    loggingRepository.startLogging(
                        terminal = loggingTerminal,
                        startingId = logs.lastOrNull()?.id ?: 0,
                    ).catch { throwable ->
                        Timber.e(throwable)

                        if (throwable is TerminalNotSupportedException) {
                            if (appPreferences.fallbackToDefaultTerminal) {
                                toast(Strings.terminal_unavailable_falling_back)

                                loggingTerminal.exit()
                                loggingTerminal = terminals[DefaultTerminal.INDEX]
                            } else {
                                delay(10000) // waiting for 10sec before new attempt
                            }
                        } else {
                            toast(getString(Strings.error, throwable.localizedMessage))
                            throwable.printStackTrace()

                            delay(10000) // waiting for 10sec before new attempt
                        }
                    }.collect { logLine ->
                        withContext(defaultDispatcher) {
                            logsMutex.withLock {
                                logs.add(logLine)

                                while (logs.size > appPreferences.logsDisplayLimit)
                                    logs.removeFirst()
                            }

                            crashesController.readers.forEach {
                                it(logLine)
                            }

                            if (logLine.suits(filtersState.value)) {
                                recordingController.reader(logLine)
                            }
                        }
                    }
                }
            } finally {
                Timber.d("finally block")
                withContext(NonCancellable) {
                    recordingController.loggingStopped()
                    clearLogs().join()

                    loggingTerminal.exit()
                }
            }
        }
    }

    private fun restartLogging() = lifecycleScope.launch {
        Timber.d("restaring logs")

        loggingJob?.cancelAndJoin()
        Timber.d("cancelled loggingJob")

        startLogging()
    }

    private fun clearLogs() = lifecycleScope.launch {
        Timber.d("clearing logs")
        logsMutex.withLock {
            logs.clear()
        }

        loggingStore.updateLogs(emptyList())
    }

    private fun notification() = NotificationCompat.Builder(this, LOGGING_STATUS_CHANNEL_ID)
        .setContentTitle(getString(Strings.logging))
        .setSmallIcon(Icons.ic_logfox)
        .setOngoing(true)
        .setContentIntent(mainActivityPendingIntentProvider.provide(OPEN_APP_INTENT_ID))
        .addAction(
            Icons.ic_clear,
            getString(Strings.exit),
            makeServicePendingIntent<LoggingService>(EXIT_APP_INTENT_ID) {
                action = ACTION_KILL_SERVICE
            }
        )
        .build()

    private fun killApp() = lifecycleScope.launch {
        Timber.d("killing app")

        loggingJob?.cancelAndJoin()
        Timber.d("cancelled loggingJob and now can stop app")

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
