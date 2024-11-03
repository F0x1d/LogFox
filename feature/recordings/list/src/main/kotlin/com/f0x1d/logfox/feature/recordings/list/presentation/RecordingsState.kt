package com.f0x1d.logfox.feature.recordings.list.presentation

import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState

data class RecordingsState(
    val recordings: List<LogRecording> = emptyList(),
    val recordingState: RecordingState = RecordingState.IDLE,
)
