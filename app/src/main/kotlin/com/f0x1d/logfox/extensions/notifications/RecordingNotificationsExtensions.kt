package com.f0x1d.logfox.extensions.notifications

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.context.RECORDING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.context.doIfNotificationsAllowed
import com.f0x1d.logfox.context.notificationManagerCompat
import com.f0x1d.logfox.intents.PAUSE_RECORDING_INTENT_ID
import com.f0x1d.logfox.intents.RESUME_RECORDING_INTENT_ID
import com.f0x1d.logfox.intents.STOP_RECORDING_INTENT_ID
import com.f0x1d.logfox.intents.makeBroadcastPendingIntent
import com.f0x1d.logfox.receiver.RecordingReceiver

private const val RECORDING_NOTIFICATIONS_TAG = "recording"
private const val RECORDING_NOTIFICATIONS_ID = 0

@SuppressLint("MissingPermission")
fun Context.sendRecordingNotification() = doIfNotificationsAllowed {
    notify(
        RECORDING_NOTIFICATIONS_TAG,
        RECORDING_NOTIFICATIONS_ID,
        NotificationCompat.Builder(this@sendRecordingNotification, RECORDING_STATUS_CHANNEL_ID)
            .setContentTitle(getString(R.string.recording))
            .setSmallIcon(R.drawable.ic_recording_notification)
            .addAction(
                R.drawable.ic_pause,
                getString(R.string.pause),
                makeBroadcastPendingIntent(PAUSE_RECORDING_INTENT_ID, RecordingReceiver::class.java) {
                    action = RecordingReceiver.ACTION_PAUSE_LOGGING
                }
            )
            .addAction(
                R.drawable.ic_stop,
                getString(R.string.stop),
                makeBroadcastPendingIntent(STOP_RECORDING_INTENT_ID, RecordingReceiver::class.java) {
                    action = RecordingReceiver.ACTION_STOP_LOGGING
                }
            )
            .setOngoing(true)
            .setSilent(true)
            .build()
    )
}

@SuppressLint("MissingPermission")
fun Context.sendRecordingPausedNotification() = doIfNotificationsAllowed {
    notify(
        RECORDING_NOTIFICATIONS_TAG,
        RECORDING_NOTIFICATIONS_ID,
        NotificationCompat.Builder(this@sendRecordingPausedNotification, RECORDING_STATUS_CHANNEL_ID)
            .setContentTitle(getString(R.string.recording_paused))
            .setSmallIcon(R.drawable.ic_recording_play_notification)
            .addAction(
                R.drawable.ic_play,
                getString(R.string.resume),
                makeBroadcastPendingIntent(RESUME_RECORDING_INTENT_ID, RecordingReceiver::class.java) {
                    action = RecordingReceiver.ACTION_RESUME_LOGGING
                }
            )
            .addAction(
                R.drawable.ic_stop,
                getString(R.string.stop),
                makeBroadcastPendingIntent(STOP_RECORDING_INTENT_ID, RecordingReceiver::class.java) {
                    action = RecordingReceiver.ACTION_STOP_LOGGING
                }
            )
            .setDeleteIntent(
                makeBroadcastPendingIntent(STOP_RECORDING_INTENT_ID, RecordingReceiver::class.java) {
                    action = RecordingReceiver.ACTION_STOP_LOGGING
                }
            )
            .setSilent(true)
            .build()
    )
}

fun Context.cancelRecordingNotification() = notificationManagerCompat.cancel(
    RECORDING_NOTIFICATIONS_TAG,
    RECORDING_NOTIFICATIONS_ID
)
