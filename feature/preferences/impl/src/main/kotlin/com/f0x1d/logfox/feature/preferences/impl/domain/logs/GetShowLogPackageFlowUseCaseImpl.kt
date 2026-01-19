package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogPackageFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetShowLogPackageFlowUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogPackageFlowUseCase {

    override fun invoke(): Flow<Boolean> = logsSettingsRepository.showLogPackage()
}
