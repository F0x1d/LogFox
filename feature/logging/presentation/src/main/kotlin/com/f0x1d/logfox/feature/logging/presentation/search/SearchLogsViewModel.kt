package com.f0x1d.logfox.feature.logging.presentation.search

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.logging.api.domain.GetCaseSensitiveUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SearchLogsViewModel @Inject constructor(
    reducer: SearchLogsReducer,
    effectHandler: SearchLogsEffectHandler,
    viewStateMapper: SearchLogsViewStateMapper,
    getQueryUseCase: GetQueryUseCase,
    getCaseSensitiveUseCase: GetCaseSensitiveUseCase,
) : BaseStoreViewModel<SearchLogsViewState, SearchLogsState, SearchLogsCommand, SearchLogsSideEffect>(
    initialState = SearchLogsState(
        query = getQueryUseCase(),
        caseSensitive = getCaseSensitiveUseCase(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(
        SearchLogsSideEffect.LoadQuery,
        SearchLogsSideEffect.LoadCaseSensitive,
    ),
)
