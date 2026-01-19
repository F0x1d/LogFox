package com.f0x1d.logfox.feature.preferences.impl.domain.logs

import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogUidFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetShowLogUidFlowUseCaseImpl @Inject constructor(
    private val logsSettingsRepository: LogsSettingsRepository,
) : GetShowLogUidFlowUseCase {

    override fun invoke(): Flow<Boolean> = logsSettingsRepository.showLogUid()
}
