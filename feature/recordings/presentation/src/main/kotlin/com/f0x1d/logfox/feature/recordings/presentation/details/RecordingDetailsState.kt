package com.f0x1d.logfox.feature.recordings.presentation.details

import com.f0x1d.logfox.feature.recordings.presentation.model.LogRecordingItem

data class RecordingDetailsState(
    val recordingItem: LogRecordingItem? = null,
    val currentTitle: String? = null,
)
