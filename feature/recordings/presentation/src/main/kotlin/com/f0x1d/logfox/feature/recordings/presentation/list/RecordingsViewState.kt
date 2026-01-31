package com.f0x1d.logfox.feature.recordings.presentation.list

import androidx.compose.runtime.Immutable
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.presentation.model.LogRecordingItem

@Immutable
internal data class RecordingsViewState(
    val recordings: List<LogRecordingItem>,
    val recordingState: RecordingState,
)
