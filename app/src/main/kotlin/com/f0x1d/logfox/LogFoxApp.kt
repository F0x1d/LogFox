package com.f0x1d.logfox

import android.app.Application
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.f0x1d.logfox.context.LOGGING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.context.RECORDING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.context.notificationManagerCompat
import com.f0x1d.logfox.strings.Strings
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LogFoxApp: Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        notificationManagerCompat.apply {
            val loggingStatusChannel = NotificationChannelCompat.Builder(
                LOGGING_STATUS_CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_MIN
            )
                .setName(getString(Strings.logging_status))
                .setShowBadge(false)
                .build()

            val recordingStatusChannel = NotificationChannelCompat.Builder(
                RECORDING_STATUS_CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            )
                .setName(getString(Strings.recording_status))
                .setLightsEnabled(false)
                .setVibrationEnabled(false)
                .setSound(null, null)
                .build()

            createNotificationChannelsCompat(
                listOf(
                    loggingStatusChannel,
                    recordingStatusChannel
                )
            )
        }
    }
}
