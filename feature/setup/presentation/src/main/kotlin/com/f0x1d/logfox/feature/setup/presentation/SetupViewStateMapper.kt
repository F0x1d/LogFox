package com.f0x1d.logfox.feature.setup.presentation

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class SetupViewStateMapper @Inject constructor() : ViewStateMapper<SetupState, SetupViewState> {
    override fun map(state: SetupState) = SetupViewState(
        showAdbDialog = state.showAdbDialog,
        adbCommand = state.adbCommand,
    )
}
