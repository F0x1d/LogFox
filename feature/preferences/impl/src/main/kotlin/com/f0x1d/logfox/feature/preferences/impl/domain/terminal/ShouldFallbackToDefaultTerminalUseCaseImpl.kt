package com.f0x1d.logfox.feature.preferences.impl.domain.terminal

import com.f0x1d.logfox.feature.preferences.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.terminal.ShouldFallbackToDefaultTerminalUseCase
import javax.inject.Inject

internal class ShouldFallbackToDefaultTerminalUseCaseImpl @Inject constructor(
    private val terminalSettingsRepository: TerminalSettingsRepository,
) : ShouldFallbackToDefaultTerminalUseCase {

    override fun invoke(): Boolean = terminalSettingsRepository.fallbackToDefaultTerminal().value
}
