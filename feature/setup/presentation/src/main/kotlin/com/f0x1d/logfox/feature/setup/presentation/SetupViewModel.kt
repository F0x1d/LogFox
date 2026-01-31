package com.f0x1d.logfox.feature.setup.presentation

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.core.tea.ViewStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SetupViewModel @Inject constructor(
    reducer: SetupReducer,
    effectHandler: SetupEffectHandler,
) : BaseStoreViewModel<SetupState, SetupState, SetupCommand, SetupSideEffect>(
    initialState = SetupState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = ViewStateMapper.identity(),
)
