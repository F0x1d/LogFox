package com.f0x1d.logfox.presentation

sealed interface MainCommand {
    data object ShowSetup : MainCommand
    data object MarkNotificationsPermissionAsked : MainCommand
}
