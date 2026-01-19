package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.domain.NotifyLoggingStoppedUseCase
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingLocalDataSource
import javax.inject.Inject

internal class NotifyLoggingStoppedUseCaseImpl @Inject constructor(
    private val recordingLocalDataSource: RecordingLocalDataSource,
) : NotifyLoggingStoppedUseCase {
    override suspend fun invoke() {
        recordingLocalDataSource.loggingStopped()
    }
}
