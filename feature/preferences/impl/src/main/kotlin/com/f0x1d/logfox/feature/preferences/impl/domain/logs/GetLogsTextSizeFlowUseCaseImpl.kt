package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsTextSizeFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetLogsTextSizeFlowUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetLogsTextSizeFlowUseCase {

    override fun invoke(): Flow<Int> = logsSettingsRepository.logsTextSize()
}
