package com.f0x1d.logfox.feature.logging.presentation.extended

sealed interface LogsExtendedCopySideEffect {
    // Business logic side effects - handled by EffectHandler
    data object LoadSelectedLines : LogsExtendedCopySideEffect
}
