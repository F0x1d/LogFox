package com.f0x1d.logfox.presentation

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class MainViewStateMapper @Inject constructor() : ViewStateMapper<MainState, MainViewState> {
    override fun map(state: MainState) = MainViewState(
        askedNotificationsPermission = state.askedNotificationsPermission,
        openCrashesOnStartup = state.openCrashesOnStartup,
    )
}
