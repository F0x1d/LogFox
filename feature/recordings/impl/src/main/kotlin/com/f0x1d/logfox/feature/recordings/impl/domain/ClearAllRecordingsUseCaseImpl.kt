package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.ClearAllRecordingsUseCase
import javax.inject.Inject

internal class ClearAllRecordingsUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
) : ClearAllRecordingsUseCase {
    override suspend fun invoke() {
        recordingsRepository.clear()
    }
}
