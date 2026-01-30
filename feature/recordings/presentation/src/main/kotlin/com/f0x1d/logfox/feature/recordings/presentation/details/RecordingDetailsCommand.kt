package com.f0x1d.logfox.feature.recordings.presentation.details

import android.net.Uri
import com.f0x1d.logfox.feature.recordings.api.model.LogRecording

sealed interface RecordingDetailsCommand {
    data object Load : RecordingDetailsCommand
    data class RecordingLoaded(val recording: LogRecording?) : RecordingDetailsCommand
    data class ExportFile(val uri: Uri) : RecordingDetailsCommand
    data class ExportZipFile(val uri: Uri) : RecordingDetailsCommand
    data class UpdateTitle(val title: String) : RecordingDetailsCommand
    data object ExportFileClicked : RecordingDetailsCommand
    data object ExportZipClicked : RecordingDetailsCommand
    data object ShareRecording : RecordingDetailsCommand
}
