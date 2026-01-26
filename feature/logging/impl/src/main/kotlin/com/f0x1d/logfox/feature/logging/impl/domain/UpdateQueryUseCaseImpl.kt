package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.domain.UpdateQueryUseCase
import com.f0x1d.logfox.feature.logging.impl.data.SearchDataSource
import javax.inject.Inject

internal class UpdateQueryUseCaseImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
) : UpdateQueryUseCase {
    override suspend fun invoke(query: String?) {
        searchDataSource.updateQuery(query)
    }
}
