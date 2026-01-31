package com.f0x1d.logfox.feature.preferences.presentation.ui.settings

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class PreferencesUIViewStateMapper @Inject constructor() :
    ViewStateMapper<PreferencesUIState, PreferencesUIViewState> {

    override fun map(state: PreferencesUIState) = PreferencesUIViewState(
        nightTheme = state.nightTheme,
        dateFormat = state.dateFormat,
        timeFormat = state.timeFormat,
        showLogDate = state.showLogDate,
        showLogTime = state.showLogTime,
        showLogUid = state.showLogUid,
        showLogPid = state.showLogPid,
        showLogTid = state.showLogTid,
        showLogPackage = state.showLogPackage,
        showLogTag = state.showLogTag,
        showLogContent = state.showLogContent,
        logsUpdateInterval = state.logsUpdateInterval,
        logsTextSize = state.logsTextSize,
        logsDisplayLimit = state.logsDisplayLimit,
    )
}
