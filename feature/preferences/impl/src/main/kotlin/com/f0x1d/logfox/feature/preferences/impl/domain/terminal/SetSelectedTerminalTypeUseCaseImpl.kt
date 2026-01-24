package com.f0x1d.logfox.feature.preferences.impl.domain.terminal

import com.f0x1d.logfox.feature.preferences.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.terminal.SetSelectedTerminalTypeUseCase
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import javax.inject.Inject

internal class SetSelectedTerminalTypeUseCaseImpl @Inject constructor(
    private val terminalSettingsRepository: TerminalSettingsRepository,
) : SetSelectedTerminalTypeUseCase {

    override fun invoke(type: TerminalType) {
        terminalSettingsRepository.selectedTerminalType().set(type)
    }
}
