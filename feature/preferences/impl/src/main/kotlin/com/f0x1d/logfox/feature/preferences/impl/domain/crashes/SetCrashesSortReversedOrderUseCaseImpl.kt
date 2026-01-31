package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.api.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.SetCrashesSortReversedOrderUseCase
import javax.inject.Inject

internal class SetCrashesSortReversedOrderUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : SetCrashesSortReversedOrderUseCase {

    override fun invoke(reversed: Boolean) {
        crashesSettingsRepository.crashesSortReversedOrder().set(reversed)
    }
}
