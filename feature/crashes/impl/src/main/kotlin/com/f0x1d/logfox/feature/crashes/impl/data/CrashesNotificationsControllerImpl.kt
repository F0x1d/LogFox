package com.f0x1d.logfox.feature.crashes.impl.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.f0x1d.logfox.arch.COPY_CRASH_INTENT_ID
import com.f0x1d.logfox.arch.CRASHES_CHANNEL_GROUP_ID
import com.f0x1d.logfox.arch.doIfNotificationsAllowed
import com.f0x1d.logfox.arch.makeBroadcastPendingIntent
import com.f0x1d.logfox.arch.notificationManager
import com.f0x1d.logfox.arch.notificationManagerCompat
import com.f0x1d.logfox.arch.receiver.CopyReceiver
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.feature.crashes.api.data.CrashesNotificationsController
import com.f0x1d.logfox.feature.crashes.api.data.notificationChannelId
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.navigation.NavGraphs
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@SuppressLint("MissingPermission")
internal class CrashesNotificationsControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences,
) : CrashesNotificationsController {

    override fun sendErrorNotification(appCrash: AppCrash, crashLog: String?) {
        createNotificationChannelFor(appCrash)

        context.doIfNotificationsAllowed {
            notify(
                appCrash.packageName,
                appCrash.notificationId,
                NotificationCompat.Builder(
                    context,
                    if (appPreferences.useSeparateNotificationsChannelsForCrashes) {
                        appCrash.notificationChannelId
                    } else {
                        CRASHES_CHANNEL_ID
                    },
                )
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
                                .setGraph(NavGraphs.nav_graph)
                                .setDestination(Directions.crashDetailsFragment)
                                .setArguments(bundleOf("crash_id" to appCrash.id))
                                .createPendingIntent()
                        )
                    }
                    .setAutoCancel(true)
                    .addAction(
                        Icons.ic_copy,
                        context.getString(android.R.string.copy),
                        context.makeBroadcastPendingIntent<CopyReceiver>(
                            id = COPY_CRASH_INTENT_ID,
                            extras = bundleOf(
                                Intent.EXTRA_TEXT to crashLog,
                                CopyReceiver.EXTRA_PACKAGE_NAME to appCrash.packageName,
                                CopyReceiver.EXTRA_NOTIFICATION_ID to appCrash.notificationId,
                            )
                        )
                    )
                    .build()
            )
        }
    }

    private fun createNotificationChannelFor(appCrash: AppCrash) {
        if (appPreferences.useSeparateNotificationsChannelsForCrashes) {
            val crashTypeName = appCrash.crashType.readableName
            val groupId = "${CRASHES_CHANNEL_GROUP_ID}_$crashTypeName"

            val crashesGroup = NotificationChannelGroupCompat.Builder(groupId)
                .setName(context.getString(Strings.type_crashes, crashTypeName))
                .build()

            val appCrashesChannel = NotificationChannelCompat.Builder(
                appCrash.notificationChannelId,
                NotificationManagerCompat.IMPORTANCE_HIGH,
            )
                .setName(context.getString(Strings.crashes_of, crashTypeName, appCrash.packageName))
                .setLightsEnabled(true)
                .setVibrationEnabled(true)
                .setGroup(groupId)
                .build()

            context.notificationManagerCompat.apply {
                createNotificationChannelGroupsCompat(listOf(crashesGroup))
                createNotificationChannelsCompat(listOf(appCrashesChannel))
            }
        } else {
            val crashesChannel = NotificationChannelCompat.Builder(
                CRASHES_CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_HIGH,
            )
                .setName(context.getString(Strings.crashes))
                .setLightsEnabled(true)
                .setVibrationEnabled(true)
                .build()

            context.notificationManagerCompat.apply {
                createNotificationChannelsCompat(listOf(crashesChannel))
            }
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

    companion object {
        private const val CRASHES_CHANNEL_ID = "crashes"
    }
}
