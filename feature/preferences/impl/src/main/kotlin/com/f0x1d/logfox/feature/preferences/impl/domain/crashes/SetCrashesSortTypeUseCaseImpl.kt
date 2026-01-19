package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.CrashesSort
import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.crashes.SetCrashesSortTypeUseCase
import javax.inject.Inject

internal class SetCrashesSortTypeUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : SetCrashesSortTypeUseCase {

    override fun invoke(sortType: CrashesSort) {
        crashesSettingsRepository.crashesSortType().set(sortType)
    }
}
