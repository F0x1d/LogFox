package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.domain.EndRecordingUseCase
import com.f0x1d.logfox.feature.recordings.api.model.LogRecording
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingLocalDataSource
import javax.inject.Inject

internal class EndRecordingUseCaseImpl @Inject constructor(
    private val recordingLocalDataSource: RecordingLocalDataSource,
) : EndRecordingUseCase {
    override suspend fun invoke(): LogRecording? = recordingLocalDataSource.end()
}
