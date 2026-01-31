package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.api.CrashesSort
import com.f0x1d.logfox.feature.preferences.api.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetCrashesSortTypeFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetCrashesSortTypeFlowUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : GetCrashesSortTypeFlowUseCase {

    override fun invoke(): Flow<CrashesSort> = crashesSettingsRepository.crashesSortType()
}
