package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.UpdateRecordingTitleUseCase
import com.f0x1d.logfox.feature.recordings.api.model.LogRecording
import javax.inject.Inject

internal class UpdateRecordingTitleUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
) : UpdateRecordingTitleUseCase {
    override suspend fun invoke(logRecording: LogRecording, title: String) {
        recordingsRepository.updateTitle(logRecording, title)
    }
}
