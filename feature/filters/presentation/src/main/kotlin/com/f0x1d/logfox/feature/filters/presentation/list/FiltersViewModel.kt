package com.f0x1d.logfox.feature.filters.presentation.list

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class FiltersViewModel @Inject constructor(
    reducer: FiltersReducer,
    effectHandler: FiltersEffectHandler,
    viewStateMapper: FiltersViewStateMapper,
) : BaseStoreViewModel<FiltersViewState, FiltersState, FiltersCommand, FiltersSideEffect>(
    initialState = FiltersState(filters = emptyList()),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(FiltersSideEffect.LoadFilters),
)
