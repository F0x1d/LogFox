package com.f0x1d.logfox.feature.recordings.presentation.list

import androidx.compose.runtime.Immutable
import com.f0x1d.logfox.feature.database.model.LogRecording
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState

@Immutable
data class RecordingsState(
    val recordings: List<LogRecording> = emptyList(),
    val recordingState: RecordingState = RecordingState.IDLE,
)
