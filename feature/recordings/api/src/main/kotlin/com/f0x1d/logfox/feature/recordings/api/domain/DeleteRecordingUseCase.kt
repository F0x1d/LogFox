package com.f0x1d.logfox.feature.recordings.api.domain

interface DeleteRecordingUseCase {
    suspend operator fun invoke(recordingId: Long)
}
