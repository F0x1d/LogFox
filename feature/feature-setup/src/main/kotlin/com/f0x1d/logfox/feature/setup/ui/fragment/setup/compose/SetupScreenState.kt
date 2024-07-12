package com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose

import androidx.compose.runtime.Immutable

data class SetupScreenState(
    val showAdbDialog: Boolean = false,
    val adbCommand: String = "",
)

@Immutable
data class SetupScreenListener(
    val onRootClick: () -> Unit,
    val onAdbClick: () -> Unit,
    val onShizukuClick: () -> Unit,
    val closeAdbDialog: () -> Unit,
    val checkPermission: () -> Unit,
    val copyCommand: () -> Unit,
)

internal val MockSetupScreenListener = SetupScreenListener(
    onRootClick = { },
    onAdbClick = { },
    onShizukuClick = { },
    closeAdbDialog = { },
    checkPermission = { },
    copyCommand = { },
)
