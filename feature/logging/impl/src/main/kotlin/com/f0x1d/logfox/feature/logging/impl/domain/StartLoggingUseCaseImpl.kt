package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.data.LoggingRepository
import com.f0x1d.logfox.feature.logging.api.domain.StartLoggingUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.preferences.data.ServiceSettingsRepository
import com.f0x1d.logfox.feature.terminals.base.Terminal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class StartLoggingUseCaseImpl @Inject constructor(
    private val loggingRepository: LoggingRepository,
    private val serviceSettingsRepository: ServiceSettingsRepository,
) : StartLoggingUseCase {

    override fun invoke(
        terminal: Terminal,
        startingId: Long,
        lastLogTime: Long?,
    ): Flow<LogLine> = loggingRepository.startLogging(
        terminal = terminal,
        startingId = startingId,
        startLogsTime = lastLogTime?.let { it + 1 }
            ?: if (serviceSettingsRepository.showLogsFromAppLaunch().value) {
                System.currentTimeMillis()
            } else {
                null
            },
    )
}
