package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetQueryFlowUseCase
import com.f0x1d.logfox.feature.logging.impl.data.QueryDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetQueryFlowUseCaseImpl @Inject constructor(
    private val queryDataSource: QueryDataSource,
) : GetQueryFlowUseCase {
    override fun invoke(): Flow<String?> = queryDataSource.query
}
