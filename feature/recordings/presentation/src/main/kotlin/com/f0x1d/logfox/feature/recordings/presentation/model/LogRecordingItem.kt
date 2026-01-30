package com.f0x1d.logfox.feature.recordings.presentation.model

import java.io.File

data class LogRecordingItem(
    val recordingId: Long,
    val title: String,
    val dateAndTime: Long,
    val file: File,
    val formattedDate: String,
)
