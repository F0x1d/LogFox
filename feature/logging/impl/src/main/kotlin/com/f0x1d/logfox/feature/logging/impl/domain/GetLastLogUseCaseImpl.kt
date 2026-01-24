package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetLastLogUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.impl.data.LogsBufferDataSource
import javax.inject.Inject

internal class GetLastLogUseCaseImpl @Inject constructor(
    private val logsBufferDataSource: LogsBufferDataSource,
) : GetLastLogUseCase {
    override suspend fun invoke(): LogLine? = logsBufferDataSource.lastLog()
}
