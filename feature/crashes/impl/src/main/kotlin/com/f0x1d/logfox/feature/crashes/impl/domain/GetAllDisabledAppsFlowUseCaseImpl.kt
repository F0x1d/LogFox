package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.data.DisabledAppsRepository
import com.f0x1d.logfox.feature.crashes.api.domain.GetAllDisabledAppsFlowUseCase
import com.f0x1d.logfox.feature.database.model.DisabledApp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetAllDisabledAppsFlowUseCaseImpl @Inject constructor(
    private val disabledAppsRepository: DisabledAppsRepository,
) : GetAllDisabledAppsFlowUseCase {
    override fun invoke(): Flow<List<DisabledApp>> = disabledAppsRepository.getAllAsFlow()
}
