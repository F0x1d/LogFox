package com.f0x1d.logfox.feature.recordings.presentation.details

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RecordingDetailsViewModel @Inject constructor(
    reducer: RecordingDetailsReducer,
    effectHandler: RecordingDetailsEffectHandler,
    viewStateMapper: RecordingDetailsViewStateMapper,
) : BaseStoreViewModel<RecordingDetailsViewState, RecordingDetailsState, RecordingDetailsCommand, RecordingDetailsSideEffect>(
    initialState = RecordingDetailsState(
        recording = null,
        currentTitle = null,
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(RecordingDetailsSideEffect.LoadRecording),
)
