package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetQueryFlowUseCase
import com.f0x1d.logfox.feature.logging.impl.data.SearchDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetQueryFlowUseCaseImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
) : GetQueryFlowUseCase {
    override fun invoke(): Flow<String?> = searchDataSource.query
}
