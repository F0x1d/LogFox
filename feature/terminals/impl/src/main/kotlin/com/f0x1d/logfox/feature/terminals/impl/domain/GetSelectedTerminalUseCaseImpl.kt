package com.f0x1d.logfox.feature.terminals.impl.domain

import com.f0x1d.logfox.feature.preferences.api.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.terminals.api.base.Terminal
import com.f0x1d.logfox.feature.terminals.api.base.TerminalType
import com.f0x1d.logfox.feature.terminals.api.domain.GetSelectedTerminalUseCase
import javax.inject.Inject

internal class GetSelectedTerminalUseCaseImpl @Inject constructor(
    private val terminals: Map<TerminalType, @JvmSuppressWildcards Terminal>,
    private val terminalSettingsRepository: TerminalSettingsRepository,
) : GetSelectedTerminalUseCase {

    override fun invoke(): Terminal = terminals.getValue(terminalSettingsRepository.selectedTerminalType().value)
}
