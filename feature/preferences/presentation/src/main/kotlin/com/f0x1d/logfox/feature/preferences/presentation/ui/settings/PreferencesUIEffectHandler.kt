package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.UISettingsRepository
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

private data class LogsPreferences(
    val showLogValues: ShowLogValues,
    val logsUpdateInterval: Long,
    val logsTextSize: Int,
    val logsDisplayLimit: Int,
)

internal class PreferencesUIEffectHandler
    @Inject
    constructor(
        private val uiSettingsRepository: UISettingsRepository,
        private val dateTimeSettingsRepository: DateTimeSettingsRepository,
        private val logsSettingsRepository: LogsSettingsRepository,
    ) : EffectHandler<PreferencesUISideEffect, PreferencesUICommand> {
        override suspend fun handle(
            effect: PreferencesUISideEffect,
            onCommand: suspend (PreferencesUICommand) -> Unit,
        ) {
            when (effect) {
                is PreferencesUISideEffect.LoadPreferences -> {
                    combine(
                        combine(
                            uiSettingsRepository.nightThemeFlow,
                            dateTimeSettingsRepository.dateFormatFlow,
                            dateTimeSettingsRepository.timeFormatFlow,
                        ) { nightTheme, dateFormat, timeFormat ->
                            Triple(nightTheme, dateFormat, timeFormat)
                        },
                        combine(
                            logsSettingsRepository.showLogValuesFlow,
                            logsSettingsRepository.logsUpdateIntervalFlow,
                            logsSettingsRepository.logsTextSizeFlow,
                            logsSettingsRepository.logsDisplayLimitFlow,
                        ) { showLogValues, logsUpdateInterval, logsTextSize, logsDisplayLimit ->
                            LogsPreferences(showLogValues, logsUpdateInterval, logsTextSize, logsDisplayLimit)
                        },
                    ) { (nightTheme, dateFormat, timeFormat), logsPreferences ->
                        PreferencesUICommand.PreferencesLoaded(
                            nightTheme = nightTheme,
                            dateFormat = dateFormat,
                            timeFormat = timeFormat,
                            showLogValues = logsPreferences.showLogValues,
                            logsUpdateInterval = logsPreferences.logsUpdateInterval,
                            logsTextSize = logsPreferences.logsTextSize,
                            logsDisplayLimit = logsPreferences.logsDisplayLimit,
                        )
                    }.collect { command ->
                        onCommand(command)
                    }
                }

                is PreferencesUISideEffect.SaveNightTheme -> {
                    val theme =
                        if (effect.themeIndex == 0) {
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        } else {
                            effect.themeIndex
                        }
                    uiSettingsRepository.nightTheme = theme
                    AppCompatDelegate.setDefaultNightMode(theme)
                }

                is PreferencesUISideEffect.SaveDateFormat -> {
                    dateTimeSettingsRepository.dateFormat = effect.format
                }

                is PreferencesUISideEffect.SaveTimeFormat -> {
                    dateTimeSettingsRepository.timeFormat = effect.format
                }

                is PreferencesUISideEffect.SaveLogsFormat -> {
                    when (effect.which) {
                        0 -> logsSettingsRepository.showLogDate = effect.checked
                        1 -> logsSettingsRepository.showLogTime = effect.checked
                        2 -> logsSettingsRepository.showLogUid = effect.checked
                        3 -> logsSettingsRepository.showLogPid = effect.checked
                        4 -> logsSettingsRepository.showLogTid = effect.checked
                        5 -> logsSettingsRepository.showLogPackage = effect.checked
                        6 -> logsSettingsRepository.showLogTag = effect.checked
                        7 -> logsSettingsRepository.showLogContent = effect.checked
                    }
                }

                is PreferencesUISideEffect.SaveLogsUpdateInterval -> {
                    logsSettingsRepository.logsUpdateInterval = effect.interval
                }

                is PreferencesUISideEffect.SaveLogsTextSize -> {
                    logsSettingsRepository.logsTextSize = effect.size
                }

                is PreferencesUISideEffect.SaveLogsDisplayLimit -> {
                    logsSettingsRepository.logsDisplayLimit = effect.limit
                }

                // UI side effects - handled by Fragment
                is PreferencesUISideEffect.RecreateActivity -> {
                    Unit
                }
            }
        }
    }
