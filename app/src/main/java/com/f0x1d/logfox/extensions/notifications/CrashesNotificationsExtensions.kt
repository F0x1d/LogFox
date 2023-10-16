package com.f0x1d.logfox.extensions.notifications

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.extensions.*
import com.f0x1d.logfox.receiver.CopyReceiver
import com.f0x1d.logfox.ui.activity.CrashDetailsActivity

@SuppressLint("MissingPermission")
fun Context.sendErrorNotification(appCrash: AppCrash) = doIfPermitted {
    notify(
        appCrash.packageName,
        appCrash.notificationId,
        NotificationCompat.Builder(this@sendErrorNotification, LogFoxApp.CRASHES_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_crashed, appCrash.appName ?: appCrash.packageName))
            .setContentText(appCrash.log)
            .setSmallIcon(R.drawable.ic_android_notification)
            .setStyle(NotificationCompat.BigTextStyle().bigText(appCrash.log))
            .apply {
                if (appCrash.id != 0L) setContentIntent(makeActivityPendingIntent(CRASH_DETAILS_INTENT_ID, CrashDetailsActivity::class.java, bundleOf(
                    "crash_id" to appCrash.id
                )))
            }
            .setAutoCancel(true)
            .applyPrimaryColorIfNeeded(this@sendErrorNotification)
            .addAction(
                R.drawable.ic_copy,
                getString(android.R.string.copy),
                makeBroadcastPendingIntent(COPY_CRASH_INTENT_ID, CopyReceiver::class.java, bundleOf(
                    Intent.EXTRA_TEXT to appCrash.log,
                    CopyReceiver.EXTRA_PACKAGE_NAME to appCrash.packageName,
                    CopyReceiver.EXTRA_NOTIFICATION_ID to appCrash.notificationId
                ))
            )
            .build()
    )
}

fun Context.cancelCrashNotificationFor(appCrash: AppCrash) = notificationManagerCompat.cancel(appCrash.packageName, appCrash.notificationId)

fun Context.cancelAllCrashNotifications() = notificationManager.apply {
    activeNotifications.forEach {
        if (it.tag != null && it.tag.contains(".")) cancel(it.tag, it.id)
    }
}