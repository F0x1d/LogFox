package com.f0x1d.logfox.feature.crashes.core.controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.f0x1d.logfox.context.CRASHES_CHANNEL_ID
import com.f0x1d.logfox.context.doIfNotificationsAllowed
import com.f0x1d.logfox.context.notificationManager
import com.f0x1d.logfox.context.notificationManagerCompat
import com.f0x1d.logfox.context.receiver.CopyReceiver
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.intents.COPY_CRASH_INTENT_ID
import com.f0x1d.logfox.intents.makeBroadcastPendingIntent
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal interface CrashesNotificationsController {
    fun sendErrorNotification(appCrash: AppCrash, crashLog: String?)
    fun cancelCrashNotificationFor(appCrash: AppCrash)
    fun cancelAllCrashNotifications()
}

@SuppressLint("MissingPermission")
internal class CrashesNotificationsControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : CrashesNotificationsController {

    override fun sendErrorNotification(appCrash: AppCrash, crashLog: String?) {
        context.doIfNotificationsAllowed {
            notify(
                appCrash.packageName,
                appCrash.notificationId,
                NotificationCompat.Builder(context, CRASHES_CHANNEL_ID)
                    .setContentTitle(
                        context.getString(
                            Strings.app_crashed,
                            appCrash.appName ?: appCrash.packageName,
                        )
                    )
                    .setContentText(crashLog)
                    .setSmallIcon(Icons.ic_bug_notification)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(crashLog))
                    .apply {
                        if (appCrash.id != 0L) setContentIntent(
                            NavDeepLinkBuilder(context)
                                .setGraph(
                                    // It does not compile if included in <item name="..." type="navigation" />
                                    context.resources.getIdentifier(
                                        "nav_graph",
                                        "navigation",
                                        context.packageName,
                                    )
                                )
                                .setDestination(com.f0x1d.logfox.feature.crashes.core.R.id.crashDetailsFragment)
                                .setArguments(bundleOf("crash_id" to appCrash.id))
                                .createPendingIntent()
                        )
                    }
                    .setAutoCancel(true)
                    .addAction(
                        Icons.ic_copy,
                        context.getString(android.R.string.copy),
                        context.makeBroadcastPendingIntent(
                            COPY_CRASH_INTENT_ID, CopyReceiver::class.java, bundleOf(
                                Intent.EXTRA_TEXT to crashLog,
                                CopyReceiver.EXTRA_PACKAGE_NAME to appCrash.packageName,
                                CopyReceiver.EXTRA_NOTIFICATION_ID to appCrash.notificationId
                            )
                        )
                    )
                    .build()
            )
        }
    }

    override fun cancelCrashNotificationFor(appCrash: AppCrash) {
        context.notificationManagerCompat.cancel(
            appCrash.packageName,
            appCrash.notificationId
        )
    }

    override fun cancelAllCrashNotifications() = context.notificationManager.run {
        activeNotifications.forEach {
            if (it.tag != null && it.tag.contains(".")) cancel(it.tag, it.id)
        }
    }
}
