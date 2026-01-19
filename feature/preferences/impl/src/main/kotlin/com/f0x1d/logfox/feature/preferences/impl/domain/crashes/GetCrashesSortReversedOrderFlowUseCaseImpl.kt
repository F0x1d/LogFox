package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.crashes.GetCrashesSortReversedOrderFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetCrashesSortReversedOrderFlowUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : GetCrashesSortReversedOrderFlowUseCase {

    override fun invoke(): Flow<Boolean> = crashesSettingsRepository.crashesSortReversedOrder()
}
