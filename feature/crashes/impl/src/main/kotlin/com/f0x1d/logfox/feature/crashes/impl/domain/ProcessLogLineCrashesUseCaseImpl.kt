package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.domain.ProcessLogLineCrashesUseCase
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesLocalDataSource
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import javax.inject.Inject

internal class ProcessLogLineCrashesUseCaseImpl @Inject constructor(
    private val crashesLocalDataSource: CrashesLocalDataSource,
) : ProcessLogLineCrashesUseCase {
    override suspend fun invoke(logLine: LogLine) {
        crashesLocalDataSource.readers.forEach { reader ->
            reader(logLine)
        }
    }
}
