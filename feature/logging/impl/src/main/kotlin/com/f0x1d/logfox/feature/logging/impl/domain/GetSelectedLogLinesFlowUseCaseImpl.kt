package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetSelectedLogLinesFlowUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.impl.data.SelectedLogLinesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetSelectedLogLinesFlowUseCaseImpl @Inject constructor(
    private val selectedLogLinesDataSource: SelectedLogLinesDataSource,
) : GetSelectedLogLinesFlowUseCase {
    override fun invoke(): Flow<List<LogLine>> = selectedLogLinesDataSource.selectedLines
}
