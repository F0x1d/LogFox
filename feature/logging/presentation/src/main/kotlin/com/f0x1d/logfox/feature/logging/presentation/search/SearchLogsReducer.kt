package com.f0x1d.logfox.feature.logging.presentation.search

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import javax.inject.Inject

internal class SearchLogsReducer @Inject constructor() : Reducer<SearchLogsState, SearchLogsCommand, SearchLogsSideEffect> {

    override fun reduce(
        state: SearchLogsState,
        command: SearchLogsCommand,
    ): ReduceResult<SearchLogsState, SearchLogsSideEffect> = when (command) {
        is SearchLogsCommand.Load -> {
            state.withSideEffects(SearchLogsSideEffect.LoadQuery)
        }

        is SearchLogsCommand.QueryLoaded -> {
            state.copy(query = command.query).noSideEffects()
        }

        is SearchLogsCommand.CaseSensitiveLoaded -> {
            state.copy(caseSensitive = command.caseSensitive).noSideEffects()
        }

        is SearchLogsCommand.UpdateQuery -> {
            state.withSideEffects(
                SearchLogsSideEffect.SaveQuery(command.query),
                SearchLogsSideEffect.Dismiss,
            )
        }

        is SearchLogsCommand.ToggleCaseSensitive -> {
            val newCaseSensitive = !state.caseSensitive
            state.copy(caseSensitive = newCaseSensitive)
                .withSideEffects(SearchLogsSideEffect.SaveCaseSensitive(newCaseSensitive))
        }
    }
}
