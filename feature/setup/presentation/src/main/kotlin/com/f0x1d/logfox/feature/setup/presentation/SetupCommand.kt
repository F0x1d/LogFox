package com.f0x1d.logfox.feature.setup.presentation

sealed interface SetupCommand {
    data object RootClicked : SetupCommand
    data object AdbClicked : SetupCommand
    data object ShizukuClicked : SetupCommand
    data object CheckPermissionClicked : SetupCommand
    data object CopyCommandClicked : SetupCommand
    data object CloseAdbDialogClicked : SetupCommand

    // Internal commands from effect handlers
    data object RootExecutionSucceeded : SetupCommand
    data object RootExecutionFailed : SetupCommand
    data object ShizukuExecutionSucceeded : SetupCommand
    data object ShizukuExecutionFailed : SetupCommand
    data object PermissionGranted : SetupCommand
    data object PermissionNotGranted : SetupCommand
    data class ShowAdbDialog(val adbCommand: String) : SetupCommand
}
