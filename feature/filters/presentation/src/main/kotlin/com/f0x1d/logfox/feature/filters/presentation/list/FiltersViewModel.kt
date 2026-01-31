package com.f0x1d.logfox.feature.filters.presentation.list

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.core.tea.ViewStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class FiltersViewModel @Inject constructor(
    reducer: FiltersReducer,
    effectHandler: FiltersEffectHandler,
) : BaseStoreViewModel<FiltersState, FiltersState, FiltersCommand, FiltersSideEffect>(
    initialState = FiltersState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = ViewStateMapper.identity(),
    initialSideEffects = listOf(FiltersSideEffect.LoadFilters),
)
