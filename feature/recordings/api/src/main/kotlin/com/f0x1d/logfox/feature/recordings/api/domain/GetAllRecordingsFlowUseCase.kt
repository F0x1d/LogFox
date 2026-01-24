package com.f0x1d.logfox.feature.recordings.api.domain

import com.f0x1d.logfox.feature.recordings.api.model.LogRecording
import kotlinx.coroutines.flow.Flow

interface GetAllRecordingsFlowUseCase {
    operator fun invoke(): Flow<List<LogRecording>>
}
