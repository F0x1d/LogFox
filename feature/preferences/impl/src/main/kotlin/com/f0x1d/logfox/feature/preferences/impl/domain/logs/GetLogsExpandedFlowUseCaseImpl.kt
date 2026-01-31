package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsExpandedFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetLogsExpandedFlowUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetLogsExpandedFlowUseCase {

    override fun invoke(): Flow<Boolean> = logsSettingsRepository.logsExpanded()
}
