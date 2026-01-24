package com.f0x1d.logfox.feature.recordings.api.domain

import com.f0x1d.logfox.feature.recordings.api.model.LogRecording

interface DeleteRecordingUseCase {
    suspend operator fun invoke(logRecording: LogRecording)
}
