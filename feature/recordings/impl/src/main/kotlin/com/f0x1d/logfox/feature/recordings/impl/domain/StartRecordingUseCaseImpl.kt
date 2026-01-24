package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.domain.StartRecordingUseCase
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingLocalDataSource
import javax.inject.Inject

internal class StartRecordingUseCaseImpl @Inject constructor(
    private val recordingLocalDataSource: RecordingLocalDataSource,
) : StartRecordingUseCase {
    override suspend fun invoke() {
        recordingLocalDataSource.record()
    }
}
