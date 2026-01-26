package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetCaseSensitiveFlowUseCase
import com.f0x1d.logfox.feature.logging.impl.data.SearchDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetCaseSensitiveFlowUseCaseImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
) : GetCaseSensitiveFlowUseCase {
    override fun invoke(): Flow<Boolean> = searchDataSource.caseSensitive
}
