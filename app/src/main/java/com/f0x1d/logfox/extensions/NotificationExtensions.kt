package com.f0x1d.logfox.extensions

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.receiver.CopyReceiver
import com.f0x1d.logfox.ui.activity.CrashDetailsActivity

fun Context.sendErrorNotification(appCrash: AppCrash) = notificationManagerCompat.notify(
    appCrash.packageName,
    appCrash.dateAndTime.toInt(),
    NotificationCompat.Builder(this, LogFoxApp.CRASHES_CHANNEL_ID)
        .setContentTitle(getString(R.string.app_crashed, appCrash.appName ?: appCrash.packageName))
        .setContentText(appCrash.log)
        .setSmallIcon(R.drawable.ic_android)
        .setStyle(NotificationCompat.BigTextStyle().bigText(appCrash.log))
        .setContentIntent(makeActivityPendingIntent(-3, CrashDetailsActivity::class.java, bundleOf(
            "crash_id" to appCrash.id
        )))
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_copy,
            getString(android.R.string.copy),
            makeBroadcastPendingIntent(-2, CopyReceiver::class.java, bundleOf(
                Intent.EXTRA_TEXT to appCrash.log,
                CopyReceiver.EXTRA_PACKAGE_NAME to appCrash.packageName,
                CopyReceiver.EXTRA_NOTIFICATION_ID to appCrash.dateAndTime.toInt()
            ))
        )
        .build()
)