package com.f0x1d.logfox.feature.setup.presentation.ui

import androidx.compose.runtime.Immutable

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
