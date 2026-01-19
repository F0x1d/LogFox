package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.domain.GetLogsFlowUseCase
import com.f0x1d.logfox.feature.logging.impl.data.LogsDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetLogsFlowUseCaseImpl @Inject constructor(
    private val logsDataSource: LogsDataSource,
) : GetLogsFlowUseCase {
    override fun invoke(): Flow<List<LogLine>> = logsDataSource.logs
}
