package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogContentFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetShowLogContentFlowUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogContentFlowUseCase {

    override fun invoke(): Flow<Boolean> = logsSettingsRepository.showLogContent()
}
