package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.database.model.LogRecording
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.GetRecordingByIdFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetRecordingByIdFlowUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
) : GetRecordingByIdFlowUseCase {
    override fun invoke(id: Long): Flow<LogRecording?> = recordingsRepository.getByIdAsFlow(id)
}
