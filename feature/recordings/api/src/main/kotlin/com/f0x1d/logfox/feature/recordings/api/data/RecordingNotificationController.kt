package com.f0x1d.logfox.feature.recordings.api.data

interface RecordingNotificationController {

    companion object {
        const val RECORDING_NOTIFICATIONS_TAG = "recording"
        const val RECORDING_NOTIFICATIONS_ID = 0
    }

    fun sendRecordingNotification()
    fun sendRecordingPausedNotification()
    fun cancelRecordingNotification()
}
