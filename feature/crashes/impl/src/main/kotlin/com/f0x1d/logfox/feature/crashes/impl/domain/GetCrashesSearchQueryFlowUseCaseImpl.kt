package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.domain.GetCrashesSearchQueryFlowUseCase
import com.f0x1d.logfox.feature.crashes.impl.data.CrashesSearchLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetCrashesSearchQueryFlowUseCaseImpl @Inject constructor(
    private val crashesSearchLocalDataSource: CrashesSearchLocalDataSource,
) : GetCrashesSearchQueryFlowUseCase {
    override fun invoke(): Flow<String> = crashesSearchLocalDataSource.queryFlow
}
