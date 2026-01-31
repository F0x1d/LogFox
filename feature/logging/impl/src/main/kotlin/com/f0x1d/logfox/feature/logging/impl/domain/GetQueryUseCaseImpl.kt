package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetQueryUseCase
import com.f0x1d.logfox.feature.logging.impl.data.SearchDataSource
import javax.inject.Inject

internal class GetQueryUseCaseImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
) : GetQueryUseCase {
    override fun invoke(): String? = searchDataSource.query.value
}
