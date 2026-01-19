package com.f0x1d.logfox.feature.filters.presentation.list

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class FiltersViewModel @Inject constructor(
    reducer: FiltersReducer,
    effectHandler: FiltersEffectHandler,
) : BaseStoreViewModel<FiltersState, FiltersCommand, FiltersSideEffect>(
    initialState = FiltersState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffect = FiltersSideEffect.LoadFilters,
)
