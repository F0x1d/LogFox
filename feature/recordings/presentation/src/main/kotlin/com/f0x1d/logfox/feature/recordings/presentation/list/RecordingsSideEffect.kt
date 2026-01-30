package com.f0x1d.logfox.feature.recordings.presentation.list

sealed interface RecordingsSideEffect {
    // Business logic (handled by EffectHandler)
    data object LoadRecordings : RecordingsSideEffect
    data object ToggleStartStop : RecordingsSideEffect
    data object TogglePauseResume : RecordingsSideEffect
    data object ClearRecordings : RecordingsSideEffect
    data object SaveAll : RecordingsSideEffect
    data class DeleteRecording(val recordingId: Long) : RecordingsSideEffect

    // UI (handled by Fragment)
    data class ShowSnackbar(val text: String) : RecordingsSideEffect
    data class OpenRecording(val recordingId: Long) : RecordingsSideEffect
}
