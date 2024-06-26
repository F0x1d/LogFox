package com.f0x1d.logfox.feature.recordings.core.controller

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationCompat
import com.f0x1d.logfox.context.RECORDING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.context.doIfNotificationsAllowed
import com.f0x1d.logfox.context.notificationManagerCompat
import com.f0x1d.logfox.feature.recordings.core.receiver.RecordingReceiver
import com.f0x1d.logfox.intents.PAUSE_RECORDING_INTENT_ID
import com.f0x1d.logfox.intents.RESUME_RECORDING_INTENT_ID
import com.f0x1d.logfox.intents.STOP_RECORDING_INTENT_ID
import com.f0x1d.logfox.intents.makeBroadcastPendingIntent
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface RecordingNotificationController {

    companion object {
        internal const val RECORDING_NOTIFICATIONS_TAG = "recording"
        internal const val RECORDING_NOTIFICATIONS_ID = 0
    }

    fun sendRecordingNotification()
    fun sendRecordingPausedNotification()
    fun cancelRecordingNotification()
}

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
                    context.makeBroadcastPendingIntent(PAUSE_RECORDING_INTENT_ID, RecordingReceiver::class.java) {
                        action = RecordingReceiver.ACTION_PAUSE_LOGGING
                    }
                )
                .addAction(
                    Icons.ic_stop,
                    context.getString(Strings.stop),
                    context.makeBroadcastPendingIntent(STOP_RECORDING_INTENT_ID, RecordingReceiver::class.java) {
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
                    context.makeBroadcastPendingIntent(RESUME_RECORDING_INTENT_ID, RecordingReceiver::class.java) {
                        action = RecordingReceiver.ACTION_RESUME_LOGGING
                    }
                )
                .addAction(
                    Icons.ic_stop,
                    context.getString(Strings.stop),
                    context.makeBroadcastPendingIntent(STOP_RECORDING_INTENT_ID, RecordingReceiver::class.java) {
                        action = RecordingReceiver.ACTION_STOP_LOGGING
                    }
                )
                .setDeleteIntent(
                    context.makeBroadcastPendingIntent(STOP_RECORDING_INTENT_ID, RecordingReceiver::class.java) {
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
