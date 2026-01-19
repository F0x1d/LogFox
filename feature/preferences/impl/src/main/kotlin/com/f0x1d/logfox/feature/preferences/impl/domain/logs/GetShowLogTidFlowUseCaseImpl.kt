package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogTidFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetShowLogTidFlowUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogTidFlowUseCase {

    override fun invoke(): Flow<Boolean> = logsSettingsRepository.showLogTid()
}
