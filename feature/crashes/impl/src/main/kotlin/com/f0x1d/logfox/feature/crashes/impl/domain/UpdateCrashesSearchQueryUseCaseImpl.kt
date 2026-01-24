package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.domain.UpdateCrashesSearchQueryUseCase
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesSearchLocalDataSource
import javax.inject.Inject

internal class UpdateCrashesSearchQueryUseCaseImpl @Inject constructor(
    private val crashesSearchLocalDataSource: CrashesSearchLocalDataSource,
) : UpdateCrashesSearchQueryUseCase {
    override fun invoke(query: String) {
        crashesSearchLocalDataSource.updateQuery(query)
    }
}
