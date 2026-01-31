package com.f0x1d.logfox.feature.preferences.api.domain.terminal

interface ShouldFallbackToDefaultTerminalUseCase {
    operator fun invoke(): Boolean
}
