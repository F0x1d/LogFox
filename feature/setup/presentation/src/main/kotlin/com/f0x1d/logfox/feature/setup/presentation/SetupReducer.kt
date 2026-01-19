package com.f0x1d.logfox.feature.setup.presentation

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import javax.inject.Inject

internal class SetupReducer @Inject constructor() : Reducer<SetupState, SetupCommand, SetupSideEffect> {

    override fun reduce(
        state: SetupState,
        command: SetupCommand,
    ): ReduceResult<SetupState, SetupSideEffect> = when (command) {
        is SetupCommand.RootClicked -> state.withSideEffects(
            SetupSideEffect.SelectTerminal(TerminalType.Root),
            SetupSideEffect.ExecuteRootCommand,
        )

        is SetupCommand.AdbClicked -> state.withSideEffects(
            SetupSideEffect.SelectTerminal(TerminalType.Default),
            SetupSideEffect.CheckAdbPermission,
        )

        is SetupCommand.ShizukuClicked -> state.withSideEffects(
            SetupSideEffect.SelectTerminal(TerminalType.Shizuku),
            SetupSideEffect.ExecuteShizukuCommand,
        )

        is SetupCommand.CheckPermissionClicked -> state.withSideEffects(
            SetupSideEffect.CheckPermission,
        )

        is SetupCommand.CopyCommandClicked -> state.withSideEffects(
            SetupSideEffect.CopyAdbCommand(state.adbCommand),
            SetupSideEffect.ShowSnackbar(Strings.text_copied),
        )

        is SetupCommand.CloseAdbDialogClicked -> state.copy(
            showAdbDialog = false,
        ).noSideEffects()

        is SetupCommand.RootExecutionSucceeded -> state.withSideEffects(
            SetupSideEffect.CheckPermission,
        )

        is SetupCommand.RootExecutionFailed -> state.withSideEffects(
            SetupSideEffect.ShowSnackbar(Strings.no_root),
        )

        is SetupCommand.ShizukuExecutionSucceeded -> state.withSideEffects(
            SetupSideEffect.RestartApp,
        )

        is SetupCommand.ShizukuExecutionFailed -> state.withSideEffects(
            SetupSideEffect.ShowSnackbar(Strings.shizuku_error),
        )

        is SetupCommand.PermissionGranted -> state.copy(
            showAdbDialog = false,
        ).withSideEffects(
            SetupSideEffect.RestartApp,
        )

        is SetupCommand.PermissionNotGranted -> state.withSideEffects(
            SetupSideEffect.ShowSnackbar(Strings.no_permission_detected),
        )

        is SetupCommand.ShowAdbDialog -> state.copy(
            showAdbDialog = true,
            adbCommand = command.adbCommand,
        ).noSideEffects()
    }
}
