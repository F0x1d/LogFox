package com.f0x1d.logfox.presentation

sealed interface MainCommand {
    data object Load : MainCommand
    data object ShowSetup : MainCommand
}
