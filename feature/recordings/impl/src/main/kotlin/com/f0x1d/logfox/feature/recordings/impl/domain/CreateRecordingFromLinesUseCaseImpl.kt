package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.CreateRecordingFromLinesUseCase
import javax.inject.Inject

internal class CreateRecordingFromLinesUseCaseImpl @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
) : CreateRecordingFromLinesUseCase {
    override suspend fun invoke(lines: List<LogLine>) {
        recordingsRepository.createRecordingFrom(lines)
    }
}
