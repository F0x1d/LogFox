package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.data.CrashDetectingRepository
import com.f0x1d.logfox.feature.crashes.api.domain.ProcessLogLineCrashesUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import javax.inject.Inject

internal class ProcessLogLineCrashesUseCaseImpl @Inject constructor(
    private val crashDetectingRepository: CrashDetectingRepository,
) : ProcessLogLineCrashesUseCase {

    override suspend fun invoke(logLine: LogLine) {
        crashDetectingRepository.processLogLine(logLine)
    }
}
