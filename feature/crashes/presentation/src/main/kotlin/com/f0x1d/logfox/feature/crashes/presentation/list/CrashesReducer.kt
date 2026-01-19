package com.f0x1d.logfox.feature.crashes.presentation.list

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import javax.inject.Inject

internal class CrashesReducer @Inject constructor() : Reducer<CrashesState, CrashesCommand, CrashesSideEffect> {

    override fun reduce(
        state: CrashesState,
        command: CrashesCommand,
    ): ReduceResult<CrashesState, CrashesSideEffect> = when (command) {
        is CrashesCommand.Load -> state.withSideEffects(CrashesSideEffect.LoadCrashes)

        is CrashesCommand.CrashesLoaded -> state.copy(
            crashes = command.crashes,
            currentSort = command.sortType,
            sortInReversedOrder = command.sortInReversedOrder,
        ).noSideEffects()

        is CrashesCommand.SearchedCrashesLoaded -> state.copy(
            searchedCrashes = command.searchedCrashes,
        ).noSideEffects()

        is CrashesCommand.UpdateQuery -> state.copy(
            query = command.query,
        ).withSideEffects(
            CrashesSideEffect.UpdateSearchQuery(command.query),
        )

        is CrashesCommand.UpdateSort -> state.withSideEffects(
            CrashesSideEffect.UpdateSortPreferences(
                sortType = command.sortType,
                sortInReversedOrder = command.sortInReversedOrder,
            ),
        )

        is CrashesCommand.DeleteCrashesByPackageName -> state.withSideEffects(
            CrashesSideEffect.DeleteCrashesByPackageName(command.appCrash),
        )

        is CrashesCommand.DeleteCrash -> state.withSideEffects(
            CrashesSideEffect.DeleteCrash(command.appCrash),
        )

        is CrashesCommand.ClearCrashes -> state.withSideEffects(
            CrashesSideEffect.ClearAllCrashes,
        )

        is CrashesCommand.CheckAppDisabled -> state.withSideEffects(
            CrashesSideEffect.CheckAppDisabled(
                packageName = command.packageName,
                disabled = command.disabled,
            ),
        )
    }
}
