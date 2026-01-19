package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.data.DisabledAppsRepository
import com.f0x1d.logfox.feature.crashes.api.domain.IsAppDisabledFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class IsAppDisabledFlowUseCaseImpl @Inject constructor(
    private val disabledAppsRepository: DisabledAppsRepository,
) : IsAppDisabledFlowUseCase {
    override fun invoke(packageName: String): Flow<Boolean> =
        disabledAppsRepository.disabledForFlow(packageName)
}
