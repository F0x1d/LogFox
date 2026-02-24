package com.f0x1d.logfox.feature.recordings.impl.domain

import android.net.Uri
import com.f0x1d.logfox.core.context.deviceData
import com.f0x1d.logfox.core.io.putZipEntry
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.preferences.api.domain.service.GetExportLogsAsTxtUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.service.GetIncludeDeviceInfoInArchivesUseCase
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.ExportRecordingZipUseCase
import javax.inject.Inject

internal class ExportRecordingZipUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
    private val exportRepository: ExportRepository,
    private val getIncludeDeviceInfoInArchivesUseCase: GetIncludeDeviceInfoInArchivesUseCase,
    private val getExportLogsAsTxtUseCase: GetExportLogsAsTxtUseCase,
    private val dateTimeFormatter: DateTimeFormatter,
) : ExportRecordingZipUseCase {

    override suspend fun invoke(recordingId: Long, uri: Uri) {
        val recording = recordingsRepository.getById(recordingId) ?: return
        val includeDeviceInfo = getIncludeDeviceInfoInArchivesUseCase()
        val logExtension = if (getExportLogsAsTxtUseCase()) "txt" else "log"
        val suffix = dateTimeFormatter.formatForExport(recording.dateAndTime)

        exportRepository.writeZipToUri(uri) {
            if (includeDeviceInfo) {
                putZipEntry(
                    "device_${suffix}.txt",
                    deviceData.encodeToByteArray(),
                )
            }

            putZipEntry(
                name = "recorded_${suffix}.$logExtension",
                file = recording.file,
            )
        }
    }
}
