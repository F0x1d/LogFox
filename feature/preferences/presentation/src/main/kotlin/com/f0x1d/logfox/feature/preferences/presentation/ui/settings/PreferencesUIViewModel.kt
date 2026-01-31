package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.preferences.domain.datetime.GetDateFormatUseCase
import com.f0x1d.logfox.feature.preferences.domain.datetime.GetTimeFormatUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsDisplayLimitUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsTextSizeUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetLogsUpdateIntervalUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogContentUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogDateUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogPackageUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogPidUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogTagUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogTidUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogTimeUseCase
import com.f0x1d.logfox.feature.preferences.domain.logs.GetShowLogUidUseCase
import com.f0x1d.logfox.feature.preferences.domain.ui.GetNightThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PreferencesUIViewModel @Inject constructor(
    reducer: PreferencesUIReducer,
    effectHandler: PreferencesUIEffectHandler,
    viewStateMapper: PreferencesUIViewStateMapper,
    getNightThemeUseCase: GetNightThemeUseCase,
    getDateFormatUseCase: GetDateFormatUseCase,
    getTimeFormatUseCase: GetTimeFormatUseCase,
    getShowLogDateUseCase: GetShowLogDateUseCase,
    getShowLogTimeUseCase: GetShowLogTimeUseCase,
    getShowLogUidUseCase: GetShowLogUidUseCase,
    getShowLogPidUseCase: GetShowLogPidUseCase,
    getShowLogTidUseCase: GetShowLogTidUseCase,
    getShowLogPackageUseCase: GetShowLogPackageUseCase,
    getShowLogTagUseCase: GetShowLogTagUseCase,
    getShowLogContentUseCase: GetShowLogContentUseCase,
    getLogsUpdateIntervalUseCase: GetLogsUpdateIntervalUseCase,
    getLogsTextSizeUseCase: GetLogsTextSizeUseCase,
    getLogsDisplayLimitUseCase: GetLogsDisplayLimitUseCase,
) : BaseStoreViewModel<PreferencesUIViewState, PreferencesUIState, PreferencesUICommand, PreferencesUISideEffect>(
    initialState = PreferencesUIState(
        nightTheme = getNightThemeUseCase(),
        dateFormat = getDateFormatUseCase(),
        timeFormat = getTimeFormatUseCase(),
        showLogDate = getShowLogDateUseCase(),
        showLogTime = getShowLogTimeUseCase(),
        showLogUid = getShowLogUidUseCase(),
        showLogPid = getShowLogPidUseCase(),
        showLogTid = getShowLogTidUseCase(),
        showLogPackage = getShowLogPackageUseCase(),
        showLogTag = getShowLogTagUseCase(),
        showLogContent = getShowLogContentUseCase(),
        logsUpdateInterval = getLogsUpdateIntervalUseCase(),
        logsTextSize = getLogsTextSizeUseCase(),
        logsDisplayLimit = getLogsDisplayLimitUseCase(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(PreferencesUISideEffect.LoadPreferences),
)
