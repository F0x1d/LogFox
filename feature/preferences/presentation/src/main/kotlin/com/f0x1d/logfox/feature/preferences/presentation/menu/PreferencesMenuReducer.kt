package com.f0x1d.logfox.feature.preferences.presentation.menu

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.withSideEffects
import java.io.File
import javax.inject.Inject

internal class PreferencesMenuReducer @Inject constructor(private val timberLogFile: File) : Reducer<PreferencesMenuState, PreferencesMenuCommand, PreferencesMenuSideEffect> {

    override fun reduce(
        state: PreferencesMenuState,
        command: PreferencesMenuCommand,
    ): ReduceResult<PreferencesMenuState, PreferencesMenuSideEffect> = when (command) {
        is PreferencesMenuCommand.UISettingsClicked -> {
            state.withSideEffects(PreferencesMenuSideEffect.NavigateToUISettings)
        }

        is PreferencesMenuCommand.ServiceSettingsClicked -> {
            state.withSideEffects(PreferencesMenuSideEffect.NavigateToServiceSettings)
        }

        is PreferencesMenuCommand.CrashesSettingsClicked -> {
            state.withSideEffects(PreferencesMenuSideEffect.NavigateToCrashesSettings)
        }

        is PreferencesMenuCommand.NotificationsSettingsClicked -> {
            state.withSideEffects(PreferencesMenuSideEffect.NavigateToNotificationsSettings)
        }

        is PreferencesMenuCommand.LinksClicked -> {
            state.withSideEffects(PreferencesMenuSideEffect.NavigateToLinks)
        }

        is PreferencesMenuCommand.ShareLogsClicked -> {
            state.withSideEffects(PreferencesMenuSideEffect.ShareLogs(timberLogFile))
        }
    }
}
