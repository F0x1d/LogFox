package com.f0x1d.logfox.feature.recordings.impl.domain

import android.net.Uri
import com.f0x1d.logfox.core.context.deviceData
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.preferences.api.domain.service.GetIncludeDeviceInfoInArchivesUseCase
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.ExportRecordingFileUseCase
import javax.inject.Inject

internal class ExportRecordingFileUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
    private val exportRepository: ExportRepository,
    private val getIncludeDeviceInfoInArchivesUseCase: GetIncludeDeviceInfoInArchivesUseCase,
) : ExportRecordingFileUseCase {

    override suspend fun invoke(recordingId: Long, uri: Uri) {
        val recording = recordingsRepository.getById(recordingId) ?: return

        if (getIncludeDeviceInfoInArchivesUseCase()) {
            val prefix = deviceData + "\n\n"
            exportRepository.writeContentAndFileToUri(uri, prefix, recording.file)
        } else {
            exportRepository.copyFileToUri(uri, recording.file)
        }
    }
}
