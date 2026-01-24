package com.f0x1d.logfox.feature.recordings.presentation.list

import androidx.compose.runtime.Immutable
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.api.model.LogRecording

@Immutable
data class RecordingsState(
    val recordings: List<LogRecording> = emptyList(),
    val recordingState: RecordingState = RecordingState.IDLE,
)
