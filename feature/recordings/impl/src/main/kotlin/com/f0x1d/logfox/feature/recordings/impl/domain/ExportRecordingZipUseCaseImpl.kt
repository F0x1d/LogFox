package com.f0x1d.logfox.feature.recordings.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.preferences.domain.service.GetIncludeDeviceInfoInArchivesUseCase
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.ExportRecordingZipUseCase
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingExportDataSource
import javax.inject.Inject

internal class ExportRecordingZipUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
    private val recordingExportDataSource: RecordingExportDataSource,
    private val getIncludeDeviceInfoInArchivesUseCase: GetIncludeDeviceInfoInArchivesUseCase,
) : ExportRecordingZipUseCase {

    override suspend fun invoke(recordingId: Long, uri: Uri) {
        val recording = recordingsRepository.getById(recordingId) ?: return
        recordingExportDataSource.exportZipToUri(
            uri = uri,
            recordingFile = recording.file,
            includeDeviceInfo = getIncludeDeviceInfoInArchivesUseCase(),
        )
    }
}
