package com.f0x1d.logfox

import android.app.Application
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.f0x1d.logfox.extensions.notificationManagerCompat
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope

@HiltAndroidApp
class LogFoxApp: Application() {

    companion object {
        const val LOGGING_STATUS_CHANNEL_ID = "status"
        const val CRASHES_CHANNEL_ID = "crashes"

        val applicationScope = MainScope()
        lateinit var instance: LogFoxApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        notificationManagerCompat.apply {
            val loggingStatusChannel = NotificationChannelCompat.Builder(LOGGING_STATUS_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_MIN)
                .setName(getString(R.string.status))
                .setShowBadge(false)
                .build()

            val crashesChannel = NotificationChannelCompat.Builder(CRASHES_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName(getString(R.string.crashes))
                .setLightsEnabled(true)
                .setVibrationEnabled(true)
                .build()

            createNotificationChannelsCompat(listOf(loggingStatusChannel, crashesChannel))
        }
    }
}