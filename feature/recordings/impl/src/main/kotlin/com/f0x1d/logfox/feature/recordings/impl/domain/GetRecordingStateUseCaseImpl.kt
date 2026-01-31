package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.api.domain.GetRecordingStateUseCase
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingLocalDataSource
import javax.inject.Inject

internal class GetRecordingStateUseCaseImpl @Inject constructor(
    private val recordingLocalDataSource: RecordingLocalDataSource,
) : GetRecordingStateUseCase {
    override fun invoke(): RecordingState = recordingLocalDataSource.recordingState.value
}
