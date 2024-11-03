package com.f0x1d.logfox.feature.recordings.details.presentation

import com.f0x1d.logfox.database.entity.LogRecording

data class RecordingDetailsState(
    val recording: LogRecording? = null,
)
