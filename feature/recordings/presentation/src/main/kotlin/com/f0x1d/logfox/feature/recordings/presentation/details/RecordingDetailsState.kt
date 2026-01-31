package com.f0x1d.logfox.feature.recordings.presentation.details

import com.f0x1d.logfox.feature.recordings.api.model.LogRecording

internal data class RecordingDetailsState(
    val recording: LogRecording?,
    val currentTitle: String?,
)
