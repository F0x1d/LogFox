package com.f0x1d.logfox.feature.logging.presentation.search

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class SearchLogsViewStateMapper @Inject constructor() : ViewStateMapper<SearchLogsState, SearchLogsViewState> {
    override fun map(state: SearchLogsState): SearchLogsViewState = SearchLogsViewState(
        query = state.query,
        caseSensitive = state.caseSensitive,
    )
}
