package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetLastLogIdUseCase
import com.f0x1d.logfox.feature.logging.impl.data.LogsBufferDataSource
import javax.inject.Inject

internal class GetLastLogIdUseCaseImpl @Inject constructor(
    private val logsBufferDataSource: LogsBufferDataSource,
) : GetLastLogIdUseCase {
    override fun invoke(): Long = logsBufferDataSource.lastId()
}
