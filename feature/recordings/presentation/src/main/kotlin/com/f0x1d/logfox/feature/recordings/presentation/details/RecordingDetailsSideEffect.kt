package com.f0x1d.logfox.feature.recordings.presentation.details

import android.net.Uri
import com.f0x1d.logfox.feature.database.model.LogRecording

sealed interface RecordingDetailsSideEffect {
    // Business logic (handled by EffectHandler)
    data object LoadRecording : RecordingDetailsSideEffect
    data class ExportFile(val uri: Uri, val recording: LogRecording) : RecordingDetailsSideEffect
    data class ExportZipFile(val uri: Uri, val recording: LogRecording) : RecordingDetailsSideEffect
    data class UpdateTitle(val title: String, val recording: LogRecording) : RecordingDetailsSideEffect

    // UI (handled by Fragment) - none for this screen
}
