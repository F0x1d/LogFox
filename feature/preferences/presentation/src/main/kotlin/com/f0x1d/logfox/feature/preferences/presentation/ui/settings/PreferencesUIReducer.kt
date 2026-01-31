package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import com.f0x1d.logfox.feature.preferences.api.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import javax.inject.Inject

internal class PreferencesUIReducer
@Inject
constructor() : Reducer<PreferencesUIState, PreferencesUICommand, PreferencesUISideEffect> {
    override fun reduce(
        state: PreferencesUIState,
        command: PreferencesUICommand,
    ): ReduceResult<PreferencesUIState, PreferencesUISideEffect> = when (command) {
        is PreferencesUICommand.Load -> {
            state.withSideEffects(PreferencesUISideEffect.LoadPreferences)
        }

        is PreferencesUICommand.PreferencesLoaded -> {
            state
                .copy(
                    nightTheme = command.nightTheme,
                    dateFormat = command.dateFormat,
                    timeFormat = command.timeFormat,
                    showLogDate = command.showLogDate,
                    showLogTime = command.showLogTime,
                    showLogUid = command.showLogUid,
                    showLogPid = command.showLogPid,
                    showLogTid = command.showLogTid,
                    showLogPackage = command.showLogPackage,
                    showLogTag = command.showLogTag,
                    showLogContent = command.showLogContent,
                    logsUpdateInterval = command.logsUpdateInterval,
                    logsTextSize = command.logsTextSize,
                    logsDisplayLimit = command.logsDisplayLimit,
                ).noSideEffects()
        }

        is PreferencesUICommand.NightThemeChanged -> {
            state.copy(nightTheme = command.themeIndex).withSideEffects(
                PreferencesUISideEffect.SaveNightTheme(command.themeIndex),
                PreferencesUISideEffect.RecreateActivity,
            )
        }

        is PreferencesUICommand.MonetEnabledChanged -> {
            state.withSideEffects(PreferencesUISideEffect.RecreateActivity)
        }

        is PreferencesUICommand.DateFormatChanged -> {
            val format =
                command.format?.trim() ?: DateTimeSettingsRepository.DATE_FORMAT_DEFAULT
            state.copy(dateFormat = format).withSideEffects(
                PreferencesUISideEffect.SaveDateFormat(format),
            )
        }

        is PreferencesUICommand.TimeFormatChanged -> {
            val format =
                command.format?.trim() ?: DateTimeSettingsRepository.TIME_FORMAT_DEFAULT
            state.copy(timeFormat = format).withSideEffects(
                PreferencesUISideEffect.SaveTimeFormat(format),
            )
        }

        is PreferencesUICommand.LogsFormatChanged -> {
            state.withSideEffects(
                PreferencesUISideEffect.SaveLogsFormat(command.which, command.checked),
            )
        }

        is PreferencesUICommand.LogsUpdateIntervalChanged -> {
            val interval =
                command.interval ?: LogsSettingsRepository.LOGS_UPDATE_INTERVAL_DEFAULT
            state.copy(logsUpdateInterval = interval).withSideEffects(
                PreferencesUISideEffect.SaveLogsUpdateInterval(interval),
            )
        }

        is PreferencesUICommand.LogsTextSizeChanged -> {
            val size = command.size ?: LogsSettingsRepository.LOGS_TEXT_SIZE_DEFAULT
            state.copy(logsTextSize = size).withSideEffects(
                PreferencesUISideEffect.SaveLogsTextSize(size),
            )
        }

        is PreferencesUICommand.LogsDisplayLimitChanged -> {
            val limit =
                command.limit?.coerceAtLeast(0)
                    ?: LogsSettingsRepository.LOGS_DISPLAY_LIMIT_DEFAULT
            state.copy(logsDisplayLimit = limit).withSideEffects(
                PreferencesUISideEffect.SaveLogsDisplayLimit(limit),
            )
        }
    }
}
