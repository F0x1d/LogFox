package com.f0x1d.logfox.feature.recordings.api.domain

interface PauseRecordingUseCase {
    suspend operator fun invoke()
}
