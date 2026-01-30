package com.f0x1d.logfox.feature.recordings.presentation.details

import android.net.Uri

sealed interface RecordingDetailsSideEffect {
    // Business logic (handled by EffectHandler)
    data object LoadRecording : RecordingDetailsSideEffect
    data class ExportFile(val uri: Uri) : RecordingDetailsSideEffect
    data class ExportZipFile(val uri: Uri) : RecordingDetailsSideEffect
    data class UpdateTitle(val title: String, val recordingId: Long) : RecordingDetailsSideEffect
    data object PrepareFileExport : RecordingDetailsSideEffect
    data object PrepareZipExport : RecordingDetailsSideEffect
    data object PrepareShare : RecordingDetailsSideEffect

    // UI (handled by Fragment)
    data class LaunchFileExportPicker(val filename: String) : RecordingDetailsSideEffect
    data class LaunchZipExportPicker(val filename: String) : RecordingDetailsSideEffect
    data class ShareFile(val file: java.io.File) : RecordingDetailsSideEffect
}
