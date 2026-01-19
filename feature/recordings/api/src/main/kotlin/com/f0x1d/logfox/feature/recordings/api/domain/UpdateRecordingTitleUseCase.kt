package com.f0x1d.logfox.feature.recordings.api.domain

import com.f0x1d.logfox.feature.database.model.LogRecording

interface UpdateRecordingTitleUseCase {
    suspend operator fun invoke(logRecording: LogRecording, title: String)
}
