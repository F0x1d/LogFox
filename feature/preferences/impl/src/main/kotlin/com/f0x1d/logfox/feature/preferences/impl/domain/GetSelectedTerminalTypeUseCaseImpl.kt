package com.f0x1d.logfox.feature.preferences.impl.domain

import com.f0x1d.logfox.feature.preferences.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.GetSelectedTerminalTypeUseCase
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import javax.inject.Inject

internal class GetSelectedTerminalTypeUseCaseImpl
    @Inject
    constructor(
        private val terminalSettingsRepository: TerminalSettingsRepository,
    ) : GetSelectedTerminalTypeUseCase {
        override fun invoke(): TerminalType = terminalSettingsRepository.selectedTerminalType
    }
