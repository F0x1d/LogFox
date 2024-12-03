package com.f0x1d.logfox.feature.recordings.list.presentation

import com.f0x1d.logfox.database.entity.LogRecording

sealed interface RecordingsAction {
    data class ShowSnackbar(val text: String) : RecordingsAction
    data class OpenRecording(val recording: LogRecording) : RecordingsAction
}
