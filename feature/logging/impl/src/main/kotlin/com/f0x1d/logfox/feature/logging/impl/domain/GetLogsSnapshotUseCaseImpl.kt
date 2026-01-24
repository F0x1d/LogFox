package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetLogsSnapshotUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.impl.data.LogsBufferDataSource
import javax.inject.Inject

internal class GetLogsSnapshotUseCaseImpl @Inject constructor(
    private val logsBufferDataSource: LogsBufferDataSource,
) : GetLogsSnapshotUseCase {
    override suspend fun invoke(): List<LogLine> = logsBufferDataSource.getAll()
}
