package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.DeleteRecordingUseCase
import javax.inject.Inject

internal class DeleteRecordingUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
) : DeleteRecordingUseCase {
    override suspend fun invoke(recordingId: Long) {
        val recording = recordingsRepository.getById(recordingId) ?: return
        recordingsRepository.delete(recording)
    }
}
