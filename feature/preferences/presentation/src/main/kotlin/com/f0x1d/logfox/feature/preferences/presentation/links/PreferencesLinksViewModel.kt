package com.f0x1d.logfox.feature.preferences.presentation.links

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.core.tea.ViewStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PreferencesLinksViewModel @Inject constructor(reducer: PreferencesLinksReducer) :
    BaseStoreViewModel<PreferencesLinksState, PreferencesLinksState, PreferencesLinksCommand, PreferencesLinksSideEffect>(
        initialState = PreferencesLinksState,
        reducer = reducer,
        effectHandlers = emptyList(),
        viewStateMapper = ViewStateMapper.identity(),
    )
