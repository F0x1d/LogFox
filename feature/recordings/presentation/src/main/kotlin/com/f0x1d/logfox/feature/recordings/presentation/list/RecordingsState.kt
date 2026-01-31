package com.f0x1d.logfox.feature.recordings.presentation.list

import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.api.model.LogRecording

internal data class RecordingsState(
    val recordings: List<LogRecording>,
    val recordingState: RecordingState,
)
