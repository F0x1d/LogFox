package com.f0x1d.logfox.feature.terminals.domain

import com.f0x1d.logfox.feature.preferences.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.terminals.base.Terminal
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import javax.inject.Inject

internal class GetSelectedTerminalUseCaseImpl @Inject constructor(
    private val terminals: Map<TerminalType, @JvmSuppressWildcards Terminal>,
    private val terminalSettingsRepository: TerminalSettingsRepository,
) : GetSelectedTerminalUseCase {

    override fun invoke(): Terminal = terminals.getValue(terminalSettingsRepository.selectedTerminalType().value)
}
