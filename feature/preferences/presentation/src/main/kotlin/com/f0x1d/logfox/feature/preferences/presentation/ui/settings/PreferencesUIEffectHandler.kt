package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.UISettingsRepository
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class PreferencesUIEffectHandler @Inject constructor(
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
                        uiSettingsRepository.nightTheme(),
                        dateTimeSettingsRepository.dateFormat(),
                        dateTimeSettingsRepository.timeFormat(),
                    ) { nightTheme, dateFormat, timeFormat ->
                        Triple(nightTheme, dateFormat, timeFormat)
                    },
                    combine(
                        logsSettingsRepository.showLogDate(),
                        logsSettingsRepository.showLogTime(),
                        logsSettingsRepository.showLogUid(),
                        logsSettingsRepository.showLogPid(),
                        logsSettingsRepository.showLogTid(),
                    ) { date, time, uid, pid, tid ->
                        listOf(date, time, uid, pid, tid)
                    },
                    combine(
                        logsSettingsRepository.showLogPackage(),
                        logsSettingsRepository.showLogTag(),
                        logsSettingsRepository.showLogContent(),
                        logsSettingsRepository.logsUpdateInterval(),
                        logsSettingsRepository.logsTextSize(),
                    ) { packageName, tag, content, updateInterval, textSize ->
                        listOf(packageName, tag, content) to (updateInterval to textSize)
                    },
                    logsSettingsRepository.logsDisplayLimit(),
                ) {
                        (nightTheme, dateFormat, timeFormat),
                        showFirst,
                        (showSecond, intervals),
                        displayLimit,
                    ->
                    val showLogValues = ShowLogValues(
                        date = showFirst[0] as Boolean,
                        time = showFirst[1] as Boolean,
                        uid = showFirst[2] as Boolean,
                        pid = showFirst[3] as Boolean,
                        tid = showFirst[4] as Boolean,
                        packageName = showSecond[0] as Boolean,
                        tag = showSecond[1] as Boolean,
                        content = showSecond[2] as Boolean,
                    )
                    PreferencesUICommand.PreferencesLoaded(
                        nightTheme = nightTheme,
                        dateFormat = dateFormat,
                        timeFormat = timeFormat,
                        showLogValues = showLogValues,
                        logsUpdateInterval = intervals.first,
                        logsTextSize = intervals.second,
                        logsDisplayLimit = displayLimit,
                    )
                }.collect { command ->
                    onCommand(command)
                }
            }

            is PreferencesUISideEffect.SaveNightTheme -> {
                val theme = if (effect.themeIndex == 0) {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                } else {
                    effect.themeIndex
                }
                uiSettingsRepository.nightTheme().set(theme)
                AppCompatDelegate.setDefaultNightMode(theme)
            }

            is PreferencesUISideEffect.SaveDateFormat -> {
                dateTimeSettingsRepository.dateFormat().set(effect.format)
            }

            is PreferencesUISideEffect.SaveTimeFormat -> {
                dateTimeSettingsRepository.timeFormat().set(effect.format)
            }

            is PreferencesUISideEffect.SaveLogsFormat -> {
                when (effect.which) {
                    0 -> logsSettingsRepository.showLogDate().set(effect.checked)
                    1 -> logsSettingsRepository.showLogTime().set(effect.checked)
                    2 -> logsSettingsRepository.showLogUid().set(effect.checked)
                    3 -> logsSettingsRepository.showLogPid().set(effect.checked)
                    4 -> logsSettingsRepository.showLogTid().set(effect.checked)
                    5 -> logsSettingsRepository.showLogPackage().set(effect.checked)
                    6 -> logsSettingsRepository.showLogTag().set(effect.checked)
                    7 -> logsSettingsRepository.showLogContent().set(effect.checked)
                }
            }

            is PreferencesUISideEffect.SaveLogsUpdateInterval -> {
                logsSettingsRepository.logsUpdateInterval().set(effect.interval)
            }

            is PreferencesUISideEffect.SaveLogsTextSize -> {
                logsSettingsRepository.logsTextSize().set(effect.size)
            }

            is PreferencesUISideEffect.SaveLogsDisplayLimit -> {
                logsSettingsRepository.logsDisplayLimit().set(effect.limit)
            }

            // UI side effects - handled by Fragment
            is PreferencesUISideEffect.RecreateActivity -> {
                Unit
            }
        }
    }
}
