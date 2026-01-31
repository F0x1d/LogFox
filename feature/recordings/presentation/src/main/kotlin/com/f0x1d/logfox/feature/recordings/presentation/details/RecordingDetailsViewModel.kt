package com.f0x1d.logfox.feature.recordings.presentation.details

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.core.tea.ViewStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RecordingDetailsViewModel @Inject constructor(
    reducer: RecordingDetailsReducer,
    effectHandler: RecordingDetailsEffectHandler,
) : BaseStoreViewModel<RecordingDetailsState, RecordingDetailsState, RecordingDetailsCommand, RecordingDetailsSideEffect>(
    initialState = RecordingDetailsState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = ViewStateMapper.identity(),
    initialSideEffects = listOf(RecordingDetailsSideEffect.LoadRecording),
)
