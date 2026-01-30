package com.f0x1d.logfox.feature.recordings.impl.data

import android.net.Uri
import java.io.File

internal interface RecordingExportDataSource {
    suspend fun exportFileToUri(file: File, uri: Uri)
    suspend fun exportZipToUri(uri: Uri, recordingFile: File, includeDeviceInfo: Boolean)
}
