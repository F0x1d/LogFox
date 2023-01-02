package com.f0x1d.logfox.extensions.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.*
import com.f0x1d.logfox.receiver.RecordingReceiver

private const val RECORDING_NOTIFICATIONS_TAG = "recording"
private const val RECORDING_NOTIFICATIONS_ID = 0

fun Context.sendRecordingNotification() = notificationManagerCompat.notify(
    RECORDING_NOTIFICATIONS_TAG,
    RECORDING_NOTIFICATIONS_ID,
    NotificationCompat.Builder(this, LogFoxApp.RECORDING_STATUS_CHANNEL_ID)
        .setContentTitle(getString(R.string.recording))
        .setSmallIcon(R.drawable.ic_recording)
        .addAction(
            R.drawable.ic_pause,
            getString(R.string.pause),
            makeBroadcastPendingIntent(PAUSE_RECORDING_INTENT_ID, RecordingReceiver::class.java) { action = RecordingReceiver.ACTION_PAUSE_LOGGING }
        )
        .addAction(
            R.drawable.ic_stop,
            getString(R.string.stop),
            makeBroadcastPendingIntent(STOP_RECORDING_INTENT_ID, RecordingReceiver::class.java) { action = RecordingReceiver.ACTION_STOP_LOGGING }
        )
        .setOngoing(true)
        .setSilent(true)
        .build()
)

fun Context.sendRecordingPausedNotification() = notificationManagerCompat.notify(
    RECORDING_NOTIFICATIONS_TAG,
    RECORDING_NOTIFICATIONS_ID,
    NotificationCompat.Builder(this, LogFoxApp.RECORDING_STATUS_CHANNEL_ID)
        .setContentTitle(getString(R.string.recording_paused))
        .setSmallIcon(R.drawable.ic_recording_play)
        .addAction(
            R.drawable.ic_play,
            getString(R.string.resume),
            makeBroadcastPendingIntent(RESUME_RECORDING_INTENT_ID, RecordingReceiver::class.java) { action = RecordingReceiver.ACTION_RESUME_LOGGING }
        )
        .addAction(
            R.drawable.ic_stop,
            getString(R.string.stop),
            makeBroadcastPendingIntent(STOP_RECORDING_INTENT_ID, RecordingReceiver::class.java) { action = RecordingReceiver.ACTION_STOP_LOGGING }
        )
        .setDeleteIntent(makeBroadcastPendingIntent(STOP_RECORDING_INTENT_ID, RecordingReceiver::class.java) { action = RecordingReceiver.ACTION_STOP_LOGGING })
        .setSilent(true)
        .build()
)

fun Context.removeRecordingNotification() = notificationManagerCompat.cancel(RECORDING_NOTIFICATIONS_TAG, RECORDING_NOTIFICATIONS_ID)