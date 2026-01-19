package com.f0x1d.logfox.feature.recordings.presentation.list

import com.f0x1d.logfox.feature.database.model.LogRecording
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState

sealed interface RecordingsCommand {
    data object Load : RecordingsCommand
    data class RecordingsLoaded(
        val recordings: List<LogRecording>,
        val recordingState: RecordingState,
    ) : RecordingsCommand
    data object ToggleStartStop : RecordingsCommand
    data object TogglePauseResume : RecordingsCommand
    data object ClearRecordings : RecordingsCommand
    data object SaveAll : RecordingsCommand
    data class Delete(val recording: LogRecording) : RecordingsCommand

    // Result commands from effect handler
    data class RecordingEnded(val recording: LogRecording?) : RecordingsCommand
    data class SaveAllCompleted(val recording: LogRecording) : RecordingsCommand
    data class ShowSavingSnackbar(val text: String) : RecordingsCommand
}
