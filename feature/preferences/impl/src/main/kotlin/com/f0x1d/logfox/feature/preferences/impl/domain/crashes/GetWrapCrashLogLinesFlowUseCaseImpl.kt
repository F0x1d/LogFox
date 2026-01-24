package com.f0x1d.logfox.feature.preferences.impl.domain.crashes

import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.crashes.GetWrapCrashLogLinesFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetWrapCrashLogLinesFlowUseCaseImpl @Inject constructor(
    private val crashesSettingsRepository: CrashesSettingsRepository,
) : GetWrapCrashLogLinesFlowUseCase {

    override fun invoke(): Flow<Boolean> = crashesSettingsRepository.wrapCrashLogLines()
}
