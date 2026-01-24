package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.ClearLogsUseCase
import com.f0x1d.logfox.feature.logging.impl.data.LogsBufferDataSource
import com.f0x1d.logfox.feature.logging.impl.data.LogsDataSource
import javax.inject.Inject

internal class ClearLogsUseCaseImpl @Inject constructor(
    private val logsBufferDataSource: LogsBufferDataSource,
    private val logsDataSource: LogsDataSource,
) : ClearLogsUseCase {
    override suspend fun invoke() {
        logsBufferDataSource.clear()
        logsDataSource.updateLogs(emptyList())
    }
}
