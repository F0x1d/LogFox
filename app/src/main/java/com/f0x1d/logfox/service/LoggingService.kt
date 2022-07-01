package com.f0x1d.logfox.service

import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.f0x1d.logfox.BuildConfig
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.activityManager
import com.f0x1d.logfox.extensions.makeServicePendingIntent
import com.f0x1d.logfox.repository.logging.LoggingRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoggingService: Service() {

    companion object {
        const val ACTION_KILL_SERVICE = "${BuildConfig.APPLICATION_ID}.KILL_SERVICE"
    }

    @Inject
    lateinit var loggingRepository: LoggingRepository

    override fun onCreate() {
        super.onCreate()
        startForeground(-1, notification())
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_KILL_SERVICE -> killApp()
        }
        return START_NOT_STICKY
    }

    private fun notification() = NotificationCompat.Builder(this, LogFoxApp.LOGGING_STATUS_CHANNEL_ID)
        .setContentTitle(getString(R.string.logging))
        .setSmallIcon(R.drawable.ic_bug_notification)
        .addAction(R.drawable.ic_clear, getString(R.string.exit), makeServicePendingIntent(0, LoggingService::class.java) {
            action = ACTION_KILL_SERVICE
        })
        .build()

    private fun killApp() {
        activityManager.appTasks.forEach {
            it.finishAndRemoveTask()
        }
        loggingRepository.stopLogging()

        stopForeground(true)
        stopSelf()
    }

    override fun onBind(p0: Intent?) = null
}