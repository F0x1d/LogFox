package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.GetDateFormatFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.GetTimeFormatFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.SetDateFormatUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.SetTimeFormatUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsDisplayLimitFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsTextSizeFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsUpdateIntervalFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogContentFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogDateFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogPackageFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogPidFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTagFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTidFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTimeFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogUidFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetLogsDisplayLimitUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetLogsTextSizeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetLogsUpdateIntervalUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogContentUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogDateUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogPackageUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogPidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogTagUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogTidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogTimeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogUidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.ui.GetNightThemeFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.ui.SetNightThemeUseCase
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class PreferencesUIEffectHandler @Inject constructor(
    private val getNightThemeFlowUseCase: GetNightThemeFlowUseCase,
    private val setNightThemeUseCase: SetNightThemeUseCase,
    private val getDateFormatFlowUseCase: GetDateFormatFlowUseCase,
    private val setDateFormatUseCase: SetDateFormatUseCase,
    private val getTimeFormatFlowUseCase: GetTimeFormatFlowUseCase,
    private val setTimeFormatUseCase: SetTimeFormatUseCase,
    private val getShowLogDateFlowUseCase: GetShowLogDateFlowUseCase,
    private val setShowLogDateUseCase: SetShowLogDateUseCase,
    private val getShowLogTimeFlowUseCase: GetShowLogTimeFlowUseCase,
    private val setShowLogTimeUseCase: SetShowLogTimeUseCase,
    private val getShowLogUidFlowUseCase: GetShowLogUidFlowUseCase,
    private val setShowLogUidUseCase: SetShowLogUidUseCase,
    private val getShowLogPidFlowUseCase: GetShowLogPidFlowUseCase,
    private val setShowLogPidUseCase: SetShowLogPidUseCase,
    private val getShowLogTidFlowUseCase: GetShowLogTidFlowUseCase,
    private val setShowLogTidUseCase: SetShowLogTidUseCase,
    private val getShowLogPackageFlowUseCase: GetShowLogPackageFlowUseCase,
    private val setShowLogPackageUseCase: SetShowLogPackageUseCase,
    private val getShowLogTagFlowUseCase: GetShowLogTagFlowUseCase,
    private val setShowLogTagUseCase: SetShowLogTagUseCase,
    private val getShowLogContentFlowUseCase: GetShowLogContentFlowUseCase,
    private val setShowLogContentUseCase: SetShowLogContentUseCase,
    private val getLogsUpdateIntervalFlowUseCase: GetLogsUpdateIntervalFlowUseCase,
    private val setLogsUpdateIntervalUseCase: SetLogsUpdateIntervalUseCase,
    private val getLogsTextSizeFlowUseCase: GetLogsTextSizeFlowUseCase,
    private val setLogsTextSizeUseCase: SetLogsTextSizeUseCase,
    private val getLogsDisplayLimitFlowUseCase: GetLogsDisplayLimitFlowUseCase,
    private val setLogsDisplayLimitUseCase: SetLogsDisplayLimitUseCase,
) : EffectHandler<PreferencesUISideEffect, PreferencesUICommand> {

    override suspend fun handle(
        effect: PreferencesUISideEffect,
        onCommand: suspend (PreferencesUICommand) -> Unit,
    ) {
        when (effect) {
            is PreferencesUISideEffect.LoadPreferences -> {
                combine(
                    combine(
                        getNightThemeFlowUseCase(),
                        getDateFormatFlowUseCase(),
                        getTimeFormatFlowUseCase(),
                    ) { nightTheme, dateFormat, timeFormat ->
                        Triple(nightTheme, dateFormat, timeFormat)
                    },
                    combine(
                        getShowLogDateFlowUseCase(),
                        getShowLogTimeFlowUseCase(),
                        getShowLogUidFlowUseCase(),
                        getShowLogPidFlowUseCase(),
                        getShowLogTidFlowUseCase(),
                    ) { date, time, uid, pid, tid ->
                        listOf(date, time, uid, pid, tid)
                    },
                    combine(
                        getShowLogPackageFlowUseCase(),
                        getShowLogTagFlowUseCase(),
                        getShowLogContentFlowUseCase(),
                        getLogsUpdateIntervalFlowUseCase(),
                        getLogsTextSizeFlowUseCase(),
                    ) { packageName, tag, content, updateInterval, textSize ->
                        listOf(packageName, tag, content) to (updateInterval to textSize)
                    },
                    getLogsDisplayLimitFlowUseCase(),
                ) {
                        (nightTheme, dateFormat, timeFormat),
                        showFirst,
                        (showSecond, intervals),
                        displayLimit,
                    ->
                    PreferencesUICommand.PreferencesLoaded(
                        nightTheme = nightTheme,
                        dateFormat = dateFormat,
                        timeFormat = timeFormat,
                        showLogDate = showFirst[0] as Boolean,
                        showLogTime = showFirst[1] as Boolean,
                        showLogUid = showFirst[2] as Boolean,
                        showLogPid = showFirst[3] as Boolean,
                        showLogTid = showFirst[4] as Boolean,
                        showLogPackage = showSecond[0] as Boolean,
                        showLogTag = showSecond[1] as Boolean,
                        showLogContent = showSecond[2] as Boolean,
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
                setNightThemeUseCase(theme)
                AppCompatDelegate.setDefaultNightMode(theme)
            }

            is PreferencesUISideEffect.SaveDateFormat -> {
                setDateFormatUseCase(effect.format)
            }

            is PreferencesUISideEffect.SaveTimeFormat -> {
                setTimeFormatUseCase(effect.format)
            }

            is PreferencesUISideEffect.SaveLogsFormat -> {
                when (effect.which) {
                    0 -> setShowLogDateUseCase(effect.checked)
                    1 -> setShowLogTimeUseCase(effect.checked)
                    2 -> setShowLogUidUseCase(effect.checked)
                    3 -> setShowLogPidUseCase(effect.checked)
                    4 -> setShowLogTidUseCase(effect.checked)
                    5 -> setShowLogPackageUseCase(effect.checked)
                    6 -> setShowLogTagUseCase(effect.checked)
                    7 -> setShowLogContentUseCase(effect.checked)
                }
            }

            is PreferencesUISideEffect.SaveLogsUpdateInterval -> {
                setLogsUpdateIntervalUseCase(effect.interval)
            }

            is PreferencesUISideEffect.SaveLogsTextSize -> {
                setLogsTextSizeUseCase(effect.size)
            }

            is PreferencesUISideEffect.SaveLogsDisplayLimit -> {
                setLogsDisplayLimitUseCase(effect.limit)
            }

            // UI side effects - handled by Fragment
            is PreferencesUISideEffect.RecreateActivity -> {
                Unit
            }
        }
    }
}
