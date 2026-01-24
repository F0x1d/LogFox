package com.f0x1d.logfox.feature.recordings.presentation.details

import com.f0x1d.logfox.feature.recordings.api.model.LogRecording

data class RecordingDetailsState(
    val recording: LogRecording? = null,
    val currentTitle: String? = null,
)
