package com.f0x1d.logfox

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.f0x1d.logfox.core.context.notificationManagerCompat
import com.f0x1d.logfox.core.logging.TimberFileTree
import com.f0x1d.logfox.feature.notifications.api.LOGGING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.feature.notifications.api.RECORDING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.feature.preferences.data.UISettingsRepository
import com.f0x1d.logfox.feature.strings.Strings
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class LogFoxApp : Application(), ImageLoaderFactory {
    @Inject
    lateinit var uiSettingsRepository: UISettingsRepository

    @Inject
    lateinit var imageLoaderProvider: Provider<ImageLoader>

    @Inject
    lateinit var timberFileTree: TimberFileTree

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG && LOGGING_ENABLED) {
            Timber.plant(timberFileTree)
        }
        Timber.d("onCreate")

        AppCompatDelegate.setDefaultNightMode(uiSettingsRepository.nightTheme().value)
        DynamicColors.applyToActivitiesIfAvailable(
            this,
            DynamicColorsOptions
                .Builder()
                .setPrecondition { _, _ -> uiSettingsRepository.monetEnabled().value }
                .build(),
        )

        notificationManagerCompat.apply {
            val loggingStatusChannel =
                NotificationChannelCompat
                    .Builder(
                        LOGGING_STATUS_CHANNEL_ID,
                        NotificationManagerCompat.IMPORTANCE_MIN,
                    ).setName(getString(Strings.logging_status))
                    .setShowBadge(false)
                    .build()

            val recordingStatusChannel =
                NotificationChannelCompat
                    .Builder(
                        RECORDING_STATUS_CHANNEL_ID,
                        NotificationManagerCompat.IMPORTANCE_DEFAULT,
                    ).setName(getString(Strings.recording_status))
                    .setLightsEnabled(false)
                    .setVibrationEnabled(false)
                    .setSound(null, null)
                    .build()

            createNotificationChannelsCompat(
                listOf(
                    loggingStatusChannel,
                    recordingStatusChannel,
                ),
            )
        }
    }

    override fun newImageLoader(): ImageLoader = imageLoaderProvider.get()

    companion object {
        private const val LOGGING_ENABLED = false
    }
}
