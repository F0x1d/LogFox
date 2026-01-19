package com.f0x1d.logfox.feature.recordings.api.domain

import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import kotlinx.coroutines.flow.StateFlow

interface GetRecordingStateFlowUseCase {
    operator fun invoke(): StateFlow<RecordingState>
}
