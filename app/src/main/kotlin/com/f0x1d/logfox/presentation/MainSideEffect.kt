package com.f0x1d.logfox.presentation

sealed interface MainSideEffect {
    data object StartLoggingServiceIfNeeded : MainSideEffect
    data object OpenSetup : MainSideEffect
}
