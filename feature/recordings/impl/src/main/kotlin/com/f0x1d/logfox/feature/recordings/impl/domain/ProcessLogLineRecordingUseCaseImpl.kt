package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.recordings.api.domain.ProcessLogLineRecordingUseCase
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingLocalDataSource
import javax.inject.Inject

internal class ProcessLogLineRecordingUseCaseImpl @Inject constructor(
    private val recordingLocalDataSource: RecordingLocalDataSource,
) : ProcessLogLineRecordingUseCase {
    override suspend fun invoke(logLine: LogLine) {
        recordingLocalDataSource.reader(logLine)
    }
}
