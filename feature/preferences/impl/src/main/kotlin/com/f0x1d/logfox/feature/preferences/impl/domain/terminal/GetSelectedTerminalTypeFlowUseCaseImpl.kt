package com.f0x1d.logfox.feature.preferences.impl.domain.terminal

import com.f0x1d.logfox.feature.preferences.api.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.terminal.GetSelectedTerminalTypeFlowUseCase
import com.f0x1d.logfox.feature.terminals.api.base.TerminalType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetSelectedTerminalTypeFlowUseCaseImpl @Inject constructor(
    private val terminalSettingsRepository: TerminalSettingsRepository,
) : GetSelectedTerminalTypeFlowUseCase {

    override fun invoke(): Flow<TerminalType> = terminalSettingsRepository.selectedTerminalType()
}
