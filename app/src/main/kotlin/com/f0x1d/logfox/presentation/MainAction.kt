package com.f0x1d.logfox.presentation

sealed interface MainAction {
    data object OpenSetup : MainAction
}
