package com.f0x1d.logfox.feature.setup.impl.domain

import com.f0x1d.logfox.feature.preferences.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.setup.api.domain.SelectTerminalUseCase
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import javax.inject.Inject

internal class SelectTerminalUseCaseImpl
    @Inject
    constructor(
        private val terminalSettingsRepository: TerminalSettingsRepository,
    ) : SelectTerminalUseCase {
        override suspend fun invoke(type: TerminalType) {
            terminalSettingsRepository.selectTerminal(type)
        }
    }
