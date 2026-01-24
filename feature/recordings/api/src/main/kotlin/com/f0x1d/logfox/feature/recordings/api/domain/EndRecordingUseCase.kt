package com.f0x1d.logfox.feature.recordings.api.domain

import com.f0x1d.logfox.feature.recordings.api.model.LogRecording

interface EndRecordingUseCase {
    suspend operator fun invoke(): LogRecording?
}
