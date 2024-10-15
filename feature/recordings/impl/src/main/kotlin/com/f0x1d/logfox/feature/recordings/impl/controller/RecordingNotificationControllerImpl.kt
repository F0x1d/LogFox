package com.f0x1d.logfox.feature.recordings.impl.controller

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationCompat
import com.f0x1d.logfox.arch.PAUSE_RECORDING_INTENT_ID
import com.f0x1d.logfox.arch.RECORDING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.arch.RESUME_RECORDING_INTENT_ID
import com.f0x1d.logfox.arch.STOP_RECORDING_INTENT_ID
import com.f0x1d.logfox.arch.doIfNotificationsAllowed
import com.f0x1d.logfox.arch.makeBroadcastPendingIntent
import com.f0x1d.logfox.arch.notificationManagerCompat
import com.f0x1d.logfox.feature.recordings.api.controller.RecordingNotificationController
import com.f0x1d.logfox.feature.recordings.impl.receiver.RecordingReceiver
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@SuppressLint("MissingPermission")
internal class RecordingNotificationControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : RecordingNotificationController {

    override fun sendRecordingNotification() = context.doIfNotificationsAllowed {
        notify(
            RecordingNotificationController.RECORDING_NOTIFICATIONS_TAG,
            RecordingNotificationController.RECORDING_NOTIFICATIONS_ID,
            NotificationCompat.Builder(context, RECORDING_STATUS_CHANNEL_ID)
                .setContentTitle(context.getString(Strings.recording))
                .setSmallIcon(Icons.ic_recording_notification)
                .addAction(
                    Icons.ic_pause,
                    context.getString(Strings.pause),
                    context.makeBroadcastPendingIntent<RecordingReceiver>(PAUSE_RECORDING_INTENT_ID) {
                        action = RecordingReceiver.ACTION_PAUSE_LOGGING
                    }
                )
                .addAction(
                    Icons.ic_stop,
                    context.getString(Strings.stop),
                    context.makeBroadcastPendingIntent<RecordingReceiver>(STOP_RECORDING_INTENT_ID) {
                        action = RecordingReceiver.ACTION_STOP_LOGGING
                    }
                )
                .setOngoing(true)
                .setSilent(true)
                .build()
        )
    }

    override fun sendRecordingPausedNotification() = context.doIfNotificationsAllowed {
        notify(
            RecordingNotificationController.RECORDING_NOTIFICATIONS_TAG,
            RecordingNotificationController.RECORDING_NOTIFICATIONS_ID,
            NotificationCompat.Builder(context, RECORDING_STATUS_CHANNEL_ID)
                .setContentTitle(context.getString(Strings.recording_paused))
                .setSmallIcon(Icons.ic_recording_play_notification)
                .addAction(
                    Icons.ic_play,
                    context.getString(Strings.resume),
                    context.makeBroadcastPendingIntent<RecordingReceiver>(RESUME_RECORDING_INTENT_ID) {
                        action = RecordingReceiver.ACTION_RESUME_LOGGING
                    }
                )
                .addAction(
                    Icons.ic_stop,
                    context.getString(Strings.stop),
                    context.makeBroadcastPendingIntent<RecordingReceiver>(STOP_RECORDING_INTENT_ID) {
                        action = RecordingReceiver.ACTION_STOP_LOGGING
                    }
                )
                .setDeleteIntent(
                    context.makeBroadcastPendingIntent<RecordingReceiver>(STOP_RECORDING_INTENT_ID) {
                        action = RecordingReceiver.ACTION_STOP_LOGGING
                    }
                )
                .setSilent(true)
                .build()
        )
    }

    override fun cancelRecordingNotification() {
        context.notificationManagerCompat.cancel(
            RecordingNotificationController.RECORDING_NOTIFICATIONS_TAG,
            RecordingNotificationController.RECORDING_NOTIFICATIONS_ID,
        )
    }
}
