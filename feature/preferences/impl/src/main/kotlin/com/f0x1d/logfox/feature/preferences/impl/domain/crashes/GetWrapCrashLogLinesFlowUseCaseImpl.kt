package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.api.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetWrapCrashLogLinesFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetWrapCrashLogLinesFlowUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : GetWrapCrashLogLinesFlowUseCase {

    override fun invoke(): Flow<Boolean> = crashesSettingsRepository.wrapCrashLogLines()
}
