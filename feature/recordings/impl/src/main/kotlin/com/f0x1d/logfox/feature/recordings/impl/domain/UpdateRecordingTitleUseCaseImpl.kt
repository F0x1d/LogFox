package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.UpdateRecordingTitleUseCase
import javax.inject.Inject

internal class UpdateRecordingTitleUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
) : UpdateRecordingTitleUseCase {
    override suspend fun invoke(recordingId: Long, title: String) {
        recordingsRepository.updateTitle(recordingId, title)
    }
}
