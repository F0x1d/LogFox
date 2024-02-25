package com.f0x1d.logfox.extensions.notifications

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.extensions.*
import com.f0x1d.logfox.extensions.context.doIfNotificationsAllowed
import com.f0x1d.logfox.extensions.context.notificationManager
import com.f0x1d.logfox.extensions.context.notificationManagerCompat
import com.f0x1d.logfox.receiver.CopyReceiver

@SuppressLint("MissingPermission")
fun Context.sendErrorNotification(appCrash: AppCrash, crashLog: String?) = doIfNotificationsAllowed {
    notify(
        appCrash.packageName,
        appCrash.notificationId,
        NotificationCompat.Builder(this@sendErrorNotification, LogFoxApp.CRASHES_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_crashed, appCrash.appName ?: appCrash.packageName))
            .setContentText(crashLog)
            .setSmallIcon(R.drawable.ic_android_notification)
            .setStyle(NotificationCompat.BigTextStyle().bigText(crashLog))
            .apply {
                if (appCrash.id != 0L) setContentIntent(
                    NavDeepLinkBuilder(this@sendErrorNotification)
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.crashDetailsFragment)
                        .setArguments(bundleOf("crash_id" to appCrash.id))
                        .createPendingIntent()
                )
            }
            .setAutoCancel(true)
            .applyPrimaryColorIfNeeded(this@sendErrorNotification)
            .addAction(
                R.drawable.ic_copy,
                getString(android.R.string.copy),
                makeBroadcastPendingIntent(COPY_CRASH_INTENT_ID, CopyReceiver::class.java, bundleOf(
                    Intent.EXTRA_TEXT to crashLog,
                    CopyReceiver.EXTRA_PACKAGE_NAME to appCrash.packageName,
                    CopyReceiver.EXTRA_NOTIFICATION_ID to appCrash.notificationId
                ))
            )
            .build()
    )
}

fun Context.cancelCrashNotificationFor(appCrash: AppCrash) = notificationManagerCompat.cancel(
    appCrash.packageName,
    appCrash.notificationId
)

fun Context.cancelAllCrashNotifications() = notificationManager.apply {
    activeNotifications.forEach {
        if (it.tag != null && it.tag.contains(".")) cancel(it.tag, it.id)
    }
}