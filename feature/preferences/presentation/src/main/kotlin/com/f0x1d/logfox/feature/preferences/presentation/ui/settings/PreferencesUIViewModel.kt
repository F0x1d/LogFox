package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.GetDateFormatUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.GetTimeFormatUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsDisplayLimitUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsTextSizeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsUpdateIntervalUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogContentUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogDateUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogPackageUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogPidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTagUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTimeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogUidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.ui.GetNightThemeUseCase
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
