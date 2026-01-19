package com.f0x1d.logfox.feature.recordings.api.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine

interface CreateRecordingFromLinesUseCase {
    suspend operator fun invoke(lines: List<LogLine>)
}
