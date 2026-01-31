package com.f0x1d.logfox.feature.recordings.presentation.list

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.recordings.api.domain.GetRecordingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RecordingsViewModel @Inject constructor(
    reducer: RecordingsReducer,
    effectHandler: RecordingsEffectHandler,
    viewStateMapper: RecordingsViewStateMapper,
    getRecordingStateUseCase: GetRecordingStateUseCase,
) : BaseStoreViewModel<RecordingsViewState, RecordingsState, RecordingsCommand, RecordingsSideEffect>(
    initialState = RecordingsState(
        recordings = emptyList(),
        recordingState = getRecordingStateUseCase(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(RecordingsSideEffect.LoadRecordings),
)
