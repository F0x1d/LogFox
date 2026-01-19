package com.f0x1d.logfox.feature.recordings.impl.domain

import com.f0x1d.logfox.feature.recordings.api.domain.ResumeRecordingUseCase
import com.f0x1d.logfox.feature.recordings.impl.data.RecordingLocalDataSource
import javax.inject.Inject

internal class ResumeRecordingUseCaseImpl @Inject constructor(
    private val recordingLocalDataSource: RecordingLocalDataSource,
) : ResumeRecordingUseCase {
    override suspend fun invoke() {
        recordingLocalDataSource.resume()
    }
}
