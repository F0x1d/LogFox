package com.f0x1d.logfox.feature.preferences.presentation.service

import com.f0x1d.logfox.core.compat.isAtLeastAndroid13
import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import javax.inject.Inject

internal class PreferencesServiceReducer @Inject constructor() : Reducer<PreferencesServiceState, PreferencesServiceCommand, PreferencesServiceSideEffect> {

    override fun reduce(
        state: PreferencesServiceState,
        command: PreferencesServiceCommand,
    ): ReduceResult<PreferencesServiceState, PreferencesServiceSideEffect> = when (command) {
        is PreferencesServiceCommand.Load -> {
            state.withSideEffects(PreferencesServiceSideEffect.LoadPreferences)
        }

        is PreferencesServiceCommand.PreferencesLoaded -> {
            state.copy(
                selectedTerminalType = command.selectedTerminalType,
                terminalNames = command.terminalNames,
            ).noSideEffects()
        }

        is PreferencesServiceCommand.TerminalSelected -> {
            if (state.selectedTerminalType == command.type) {
                // Same terminal selected, just restart logging
                state.withSideEffects(PreferencesServiceSideEffect.RestartLogging)
            } else {
                // Different terminal, check if it's supported
                state.withSideEffects(
                    PreferencesServiceSideEffect.CheckTerminalSupport(command.type),
                )
            }
        }

        is PreferencesServiceCommand.TerminalSupported -> {
            state.copy(selectedTerminalType = command.type).withSideEffects(
                PreferencesServiceSideEffect.SaveTerminalType(command.type),
                PreferencesServiceSideEffect.ShowTerminalRestartDialog,
            )
        }

        is PreferencesServiceCommand.TerminalNotSupported -> {
            state.withSideEffects(PreferencesServiceSideEffect.ShowTerminalUnavailableToast)
        }

        is PreferencesServiceCommand.StartOnBootChanged -> {
            val isDefaultTerminal = state.selectedTerminalType == TerminalType.Default
            if (isAtLeastAndroid13 && command.enabled && isDefaultTerminal) {
                state.withSideEffects(PreferencesServiceSideEffect.ShowAndroid13WarningDialog)
            } else {
                state.noSideEffects()
            }
        }

        is PreferencesServiceCommand.ConfirmRestartLogging -> {
            state.withSideEffects(PreferencesServiceSideEffect.RestartLogging)
        }
    }
}
