package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.CrashesSort
import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.crashes.GetCrashesSortTypeFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetCrashesSortTypeFlowUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : GetCrashesSortTypeFlowUseCase {

    override fun invoke(): Flow<CrashesSort> = crashesSettingsRepository.crashesSortType()
}
