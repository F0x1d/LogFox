package com.f0x1d.logfox.feature.recordings.impl.domain

import android.net.Uri
import com.f0x1d.logfox.core.context.deviceData
import com.f0x1d.logfox.core.io.putZipEntry
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.preferences.api.domain.service.GetIncludeDeviceInfoInArchivesUseCase
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.ExportRecordingZipUseCase
import javax.inject.Inject

internal class ExportRecordingZipUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
    private val exportRepository: ExportRepository,
    private val getIncludeDeviceInfoInArchivesUseCase: GetIncludeDeviceInfoInArchivesUseCase,
) : ExportRecordingZipUseCase {

    override suspend fun invoke(recordingId: Long, uri: Uri) {
        val recording = recordingsRepository.getById(recordingId) ?: return
        val includeDeviceInfo = getIncludeDeviceInfoInArchivesUseCase()

        exportRepository.writeZipToUri(uri) {
            if (includeDeviceInfo) {
                putZipEntry(
                    "device.txt",
                    deviceData.encodeToByteArray(),
                )
            }

            putZipEntry(
                name = "recorded.log",
                file = recording.file,
            )
        }
    }
}
