package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsDisplayLimitFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetLogsDisplayLimitFlowUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetLogsDisplayLimitFlowUseCase {

    override fun invoke(): Flow<Int> = logsSettingsRepository.logsDisplayLimit()
}
