package com.f0x1d.logfox.feature.preferences.impl.domain.terminal

import com.f0x1d.logfox.feature.preferences.api.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.terminal.GetSelectedTerminalTypeUseCase
import com.f0x1d.logfox.feature.terminals.api.base.TerminalType
import javax.inject.Inject

internal class GetSelectedTerminalTypeUseCaseImpl @Inject constructor(
    private val terminalSettingsRepository: TerminalSettingsRepository,
) : GetSelectedTerminalTypeUseCase {

    override fun invoke(): TerminalType = terminalSettingsRepository.selectedTerminalType().value
}
