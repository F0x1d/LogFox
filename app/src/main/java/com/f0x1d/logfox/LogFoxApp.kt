package com.f0x1d.logfox

import android.app.Application
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.f0x1d.logfox.extensions.context.applyTheme
import com.f0x1d.logfox.extensions.context.notificationManagerCompat
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import javax.inject.Inject

@HiltAndroidApp
class LogFoxApp: Application() {

    companion object {
        const val LOGGING_STATUS_CHANNEL_ID = "logging"
        const val CRASHES_CHANNEL_ID = "crashes"
        const val RECORDING_STATUS_CHANNEL_ID = "recording"

        val applicationScope = MainScope()
        lateinit var instance: LogFoxApp
    }

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate() {
        super.onCreate()
        instance = this

        applyTheme(appPreferences.nightTheme)

        DynamicColors.applyToActivitiesIfAvailable(this)

        notificationManagerCompat.apply {
            val loggingStatusChannel = NotificationChannelCompat.Builder(
                LOGGING_STATUS_CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_MIN
            )
                .setName(getString(R.string.logging_status))
                .setShowBadge(false)
                .build()

            val crashesChannel = NotificationChannelCompat.Builder(
                CRASHES_CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_HIGH
            )
                .setName(getString(R.string.crashes))
                .setLightsEnabled(true)
                .setVibrationEnabled(true)
                .build()

            val recordingStatusChannel = NotificationChannelCompat.Builder(
                RECORDING_STATUS_CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            )
                .setName(getString(R.string.recording_status))
                .setLightsEnabled(false)
                .setVibrationEnabled(false)
                .setSound(null, null)
                .build()

            createNotificationChannelsCompat(
                listOf(
                    loggingStatusChannel,
                    crashesChannel,
                    recordingStatusChannel
                )
            )
        }
    }
}