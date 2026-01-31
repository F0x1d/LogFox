package com.f0x1d.logfox.feature.recordings.api.domain

import com.f0x1d.logfox.feature.recordings.api.data.RecordingState

interface GetRecordingStateUseCase {
    operator fun invoke(): RecordingState
}
