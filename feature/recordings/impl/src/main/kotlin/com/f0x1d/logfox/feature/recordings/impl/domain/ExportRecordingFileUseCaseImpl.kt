package com.f0x1d.logfox.feature.recordings.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.ExportRecordingFileUseCase
import javax.inject.Inject

internal class ExportRecordingFileUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
    private val exportRepository: ExportRepository,
) : ExportRecordingFileUseCase {

    override suspend fun invoke(recordingId: Long, uri: Uri) {
        val recording = recordingsRepository.getById(recordingId) ?: return
        exportRepository.copyFileToUri(uri, recording.file)
    }
}
