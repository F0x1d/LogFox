package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogTagFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetShowLogTagFlowUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogTagFlowUseCase {

    override fun invoke(): Flow<Boolean> = logsSettingsRepository.showLogTag()
}
