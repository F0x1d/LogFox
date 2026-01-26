package com.f0x1d.logfox.feature.logging.presentation.search

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SearchLogsViewModel @Inject constructor(
    reducer: SearchLogsReducer,
    effectHandler: SearchLogsEffectHandler,
) : BaseStoreViewModel<SearchLogsState, SearchLogsCommand, SearchLogsSideEffect>(
    initialState = SearchLogsState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffects = listOf(
        SearchLogsSideEffect.LoadQuery,
        SearchLogsSideEffect.LoadCaseSensitive,
    ),
)
