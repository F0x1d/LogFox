package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.SaveAllRecordingsUseCase
import com.f0x1d.logfox.feature.recordings.api.model.LogRecording
import javax.inject.Inject

internal class SaveAllRecordingsUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
) : SaveAllRecordingsUseCase {
    override suspend fun invoke(): LogRecording = recordingsRepository.saveAll()
}
