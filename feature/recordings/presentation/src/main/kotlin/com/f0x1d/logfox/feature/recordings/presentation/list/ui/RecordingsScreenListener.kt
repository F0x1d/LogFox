package com.f0x1d.logfox.feature.recordings.presentation.list.ui

import androidx.compose.runtime.Immutable
import com.f0x1d.logfox.feature.recordings.presentation.model.LogRecordingItem

@Immutable
internal data class RecordingsScreenListener(
    val onRecordingClick: (LogRecordingItem) -> Unit,
    val onRecordingDeleteClick: (LogRecordingItem) -> Unit,
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
