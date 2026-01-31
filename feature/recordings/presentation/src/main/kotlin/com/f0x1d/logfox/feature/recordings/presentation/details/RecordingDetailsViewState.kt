package com.f0x1d.logfox.feature.recordings.presentation.details

import com.f0x1d.logfox.feature.recordings.presentation.model.LogRecordingItem

internal data class RecordingDetailsViewState(
    val recordingItem: LogRecordingItem?,
    val currentTitle: String?,
)
