package com.f0x1d.logfox.feature.terminals.impl.domain

import com.f0x1d.logfox.feature.terminals.api.base.Terminal
import com.f0x1d.logfox.feature.terminals.api.base.TerminalType
import com.f0x1d.logfox.feature.terminals.api.domain.GetDefaultTerminalUseCase
import javax.inject.Inject

internal class GetDefaultTerminalUseCaseImpl @Inject constructor(
    private val terminals: Map<TerminalType, @JvmSuppressWildcards Terminal>,
) : GetDefaultTerminalUseCase {
    override fun invoke(): Terminal = terminals.getValue(TerminalType.Default)
}
