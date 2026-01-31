package com.f0x1d.logfox.feature.logging.presentation.extended

internal sealed interface LogsExtendedCopySideEffect {
    // Business logic side effects - handled by EffectHandler
    data object LoadSelectedLines : LogsExtendedCopySideEffect
}
