package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.UpdateLogsUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.impl.data.LogsDataSource
import javax.inject.Inject

internal class UpdateLogsUseCaseImpl @Inject constructor(
    private val logsDataSource: LogsDataSource,
) : UpdateLogsUseCase {
    override suspend fun invoke(logs: List<LogLine>) {
        logsDataSource.updateLogs(logs)
    }
}
