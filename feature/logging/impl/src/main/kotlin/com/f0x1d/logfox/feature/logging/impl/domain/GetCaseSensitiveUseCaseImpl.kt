package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.GetCaseSensitiveUseCase
import com.f0x1d.logfox.feature.logging.impl.data.SearchDataSource
import javax.inject.Inject

internal class GetCaseSensitiveUseCaseImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
) : GetCaseSensitiveUseCase {
    override fun invoke(): Boolean = searchDataSource.caseSensitive.value
}
