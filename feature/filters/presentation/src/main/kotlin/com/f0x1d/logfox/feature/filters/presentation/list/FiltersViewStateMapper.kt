package com.f0x1d.logfox.feature.filters.presentation.list

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class FiltersViewStateMapper @Inject constructor() : ViewStateMapper<FiltersState, FiltersViewState> {
    override fun map(state: FiltersState) = FiltersViewState(
        filters = state.filters,
    )
}
