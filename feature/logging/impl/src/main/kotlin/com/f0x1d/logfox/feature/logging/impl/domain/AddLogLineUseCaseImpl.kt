package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.AddLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.impl.data.LogsBufferDataSource
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsDisplayLimitUseCase
import javax.inject.Inject

internal class AddLogLineUseCaseImpl @Inject constructor(
    private val logsBufferDataSource: LogsBufferDataSource,
    private val getLogsDisplayLimitUseCase: GetLogsDisplayLimitUseCase,
) : AddLogLineUseCase {
    override suspend fun invoke(logLine: LogLine) {
        logsBufferDataSource.add(logLine, getLogsDisplayLimitUseCase())
    }
}
