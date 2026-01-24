package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.api.domain.GetRecordingStateFlowUseCase
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingLocalDataSource
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

internal class GetRecordingStateFlowUseCaseImpl @Inject constructor(
    private val recordingLocalDataSource: RecordingLocalDataSource,
) : GetRecordingStateFlowUseCase {
    override fun invoke(): StateFlow<RecordingState> = recordingLocalDataSource.recordingState
}
