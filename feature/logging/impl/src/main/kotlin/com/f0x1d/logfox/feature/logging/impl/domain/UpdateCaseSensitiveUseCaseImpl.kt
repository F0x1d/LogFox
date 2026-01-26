package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.UpdateCaseSensitiveUseCase
import com.f0x1d.logfox.feature.logging.impl.data.SearchDataSource
import javax.inject.Inject

internal class UpdateCaseSensitiveUseCaseImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
) : UpdateCaseSensitiveUseCase {
    override suspend fun invoke(caseSensitive: Boolean) {
        searchDataSource.updateCaseSensitive(caseSensitive)
    }
}
