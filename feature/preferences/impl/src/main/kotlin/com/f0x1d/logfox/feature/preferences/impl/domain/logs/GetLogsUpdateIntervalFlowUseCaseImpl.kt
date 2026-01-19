package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsUpdateIntervalFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetLogsUpdateIntervalFlowUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetLogsUpdateIntervalFlowUseCase {

    override fun invoke(): Flow<Long> = logsSettingsRepository.logsUpdateInterval()
}
