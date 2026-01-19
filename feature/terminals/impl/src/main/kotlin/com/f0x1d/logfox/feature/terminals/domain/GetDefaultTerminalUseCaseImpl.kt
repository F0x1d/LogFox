package com.f0x1d.logfox.feature.terminals.domain

import com.f0x1d.logfox.feature.terminals.base.Terminal
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import javax.inject.Inject

internal class GetDefaultTerminalUseCaseImpl @Inject constructor(
    private val terminals: Map<TerminalType, @JvmSuppressWildcards Terminal>,
) : GetDefaultTerminalUseCase {
    override fun invoke(): Terminal = terminals.getValue(TerminalType.Default)
}
