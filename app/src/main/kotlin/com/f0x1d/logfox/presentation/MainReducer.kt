package com.f0x1d.logfox.presentation

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.withSideEffects
import javax.inject.Inject

internal class MainReducer @Inject constructor() : Reducer<MainState, MainCommand, MainSideEffect> {

    override fun reduce(state: MainState, command: MainCommand): ReduceResult<MainState, MainSideEffect> =
        when (command) {
            MainCommand.Load -> state.withSideEffects(
                MainSideEffect.StartLoggingServiceIfNeeded,
            )

            MainCommand.ShowSetup -> state.withSideEffects(
                MainSideEffect.OpenSetup,
            )
        }
}
