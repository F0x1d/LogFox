package com.f0x1d.logfox.service

import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.f0x1d.logfox.BuildConfig
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.EXIT_APP_INTENT_ID
import com.f0x1d.logfox.extensions.STOP_LOGGING_SERVICE_INTENT_ID
import com.f0x1d.logfox.extensions.makeOpenAppPendingIntent
import com.f0x1d.logfox.extensions.makeServicePendingIntent
import com.f0x1d.logfox.repository.logging.LoggingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class LoggingService: Service() {

    companion object {
        const val ACTION_KILL_SERVICE = "${BuildConfig.APPLICATION_ID}.KILL_SERVICE"
        const val ACTION_STOP_SERVICE = "${BuildConfig.APPLICATION_ID}.STOP_SERVICE"
    }

    @Inject
    lateinit var loggingRepository: LoggingRepository

    override fun onCreate() {
        super.onCreate()

        loggingRepository.serviceRunningFlow.update {
            startForeground(-1, notification())
            true
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_KILL_SERVICE -> killApp()
            ACTION_STOP_SERVICE -> stopService()
        }
        return START_NOT_STICKY
    }

    private fun notification() = NotificationCompat.Builder(this, LogFoxApp.LOGGING_STATUS_CHANNEL_ID)
        .setContentTitle(getString(R.string.logging))
        .setSmallIcon(R.drawable.ic_bug_notification)
        .setContentIntent(makeOpenAppPendingIntent())
        .addAction(R.drawable.ic_stop, getString(R.string.stop_service), makeServicePendingIntent(STOP_LOGGING_SERVICE_INTENT_ID, LoggingService::class.java) {
            action = ACTION_STOP_SERVICE
        })
        .addAction(R.drawable.ic_clear, getString(R.string.exit), makeServicePendingIntent(EXIT_APP_INTENT_ID, LoggingService::class.java) {
            action = ACTION_KILL_SERVICE
        })
        .build()

    private fun stopService() {
        loggingRepository.serviceRunningFlow.update {
            false
        }

        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun killApp() {
        loggingRepository.stopLogging()
        stopService()

        exitProcess(0)
    }

    override fun onBind(p0: Intent?) = null
}