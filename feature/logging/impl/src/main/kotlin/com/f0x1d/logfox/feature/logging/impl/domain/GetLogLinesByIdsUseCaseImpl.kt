package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetLogLinesByIdsUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.impl.data.LogsDataSource
import javax.inject.Inject

internal class GetLogLinesByIdsUseCaseImpl @Inject constructor(
    private val logsDataSource: LogsDataSource,
) : GetLogLinesByIdsUseCase {
    override fun invoke(ids: Set<Long>): List<LogLine> = logsDataSource.getByIds(ids)
}
