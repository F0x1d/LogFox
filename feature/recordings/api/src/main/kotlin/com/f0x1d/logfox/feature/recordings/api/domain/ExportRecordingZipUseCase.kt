package com.f0x1d.logfox.feature.recordings.api.domain

import android.net.Uri

interface ExportRecordingZipUseCase {
    suspend operator fun invoke(recordingId: Long, uri: Uri)
}
