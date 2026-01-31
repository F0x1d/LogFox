package com.f0x1d.logfox.feature.recordings.presentation.list

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.core.tea.ViewStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RecordingsViewModel @Inject constructor(
    reducer: RecordingsReducer,
    effectHandler: RecordingsEffectHandler,
) : BaseStoreViewModel<RecordingsState, RecordingsState, RecordingsCommand, RecordingsSideEffect>(
    initialState = RecordingsState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = ViewStateMapper.identity(),
    initialSideEffects = listOf(RecordingsSideEffect.LoadRecordings),
)
