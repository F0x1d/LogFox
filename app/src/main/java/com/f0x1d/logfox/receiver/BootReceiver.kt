package com.f0x1d.logfox.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.doIfPermitted
import com.f0x1d.logfox.extensions.makeOpenAppPendingIntent
import com.f0x1d.logfox.extensions.startLoggingAndServiceIfCan
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.utils.preferences.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver: BroadcastReceiver() {

    @Inject
    lateinit var loggingRepository: LoggingRepository
    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (appPreferences.startOnBoot)
                context.startLoggingAndServiceIfCan(loggingRepository, appPreferences, true)

            if (appPreferences.showStartServiceNotificationOnBoot)
                sendStartServiceNotification(context)
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendStartServiceNotification(context: Context) {
        val notification = NotificationCompat.Builder(context, LogFoxApp.START_SERVICE_NOTIFICATIONS_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.reminder_to_start_service))
            .setContentText(context.getString(R.string.tap_here_to_start))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(context.makeOpenAppPendingIntent())
            .build()

        context.doIfPermitted {
            notify("service_start", 0, notification)
        }
    }
}

val isAtLeastAndroid13 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU