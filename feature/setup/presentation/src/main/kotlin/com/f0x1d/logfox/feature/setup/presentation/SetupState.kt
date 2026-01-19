package com.f0x1d.logfox.feature.setup.presentation

data class SetupState(
    val showAdbDialog: Boolean = false,
    val adbCommand: String = "",
)
