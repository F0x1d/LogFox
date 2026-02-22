package com.f0x1d.logfox.presentation

internal sealed interface MainSideEffect {
    data object StartLoggingServiceIfNeeded : MainSideEffect
    data object SaveNotificationsPermissionAsked : MainSideEffect
    data object HandleBackExit : MainSideEffect

    data object FinishActivity : MainSideEffect
    data object OpenSetup : MainSideEffect
}
