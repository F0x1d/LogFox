package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import javax.inject.Inject

internal class AppCrashesReducer @Inject constructor() : Reducer<AppCrashesState, AppCrashesCommand, AppCrashesSideEffect> {

    override fun reduce(
        state: AppCrashesState,
        command: AppCrashesCommand,
    ): ReduceResult<AppCrashesState, AppCrashesSideEffect> = when (command) {
        is AppCrashesCommand.CrashesLoaded -> state.copy(
            crashes = command.crashes,
        ).noSideEffects()

        is AppCrashesCommand.DeleteCrash -> state.withSideEffects(
            AppCrashesSideEffect.DeleteCrash(command.appCrash),
        )

        is AppCrashesCommand.CrashClicked -> state.withSideEffects(
            AppCrashesSideEffect.NavigateToCrashDetails(command.crashId),
        )
    }
}
