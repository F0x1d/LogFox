package com.f0x1d.logfox.feature.recordings.api.domain

import com.f0x1d.logfox.feature.recordings.api.model.LogRecording
import kotlinx.coroutines.flow.Flow

interface GetRecordingByIdFlowUseCase {
    operator fun invoke(id: Long): Flow<LogRecording?>
}
