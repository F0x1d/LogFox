package com.f0x1d.logfox.feature.recordings.list.presentation.ui

import androidx.compose.runtime.Immutable
import com.f0x1d.logfox.database.entity.LogRecording

@Immutable
internal data class RecordingsScreenListener(
    val onRecordingClick: (LogRecording) -> Unit,
    val onRecordingDeleteClick: (LogRecording) -> Unit,
    val onStartStopClick: () -> Unit,
    val onPauseResumeClick: () -> Unit,
    val onClearClick: () -> Unit,
    val onSaveAllClick: () -> Unit,
)

internal val MockRecordingsScreenListener = RecordingsScreenListener(
    onRecordingClick = { },
    onRecordingDeleteClick = { },
    onStartStopClick = { },
    onPauseResumeClick = { },
    onClearClick = { },
    onSaveAllClick = { },
)
