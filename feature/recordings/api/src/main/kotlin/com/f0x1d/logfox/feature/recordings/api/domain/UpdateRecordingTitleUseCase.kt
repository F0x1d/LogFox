package com.f0x1d.logfox.feature.recordings.api.domain

interface UpdateRecordingTitleUseCase {
    suspend operator fun invoke(recordingId: Long, title: String)
}
