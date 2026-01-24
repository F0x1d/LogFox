package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.GetAllRecordingsFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.model.LogRecording
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetAllRecordingsFlowUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
) : GetAllRecordingsFlowUseCase {
    override fun invoke(): Flow<List<LogRecording>> = recordingsRepository.getAllAsFlow()
}
