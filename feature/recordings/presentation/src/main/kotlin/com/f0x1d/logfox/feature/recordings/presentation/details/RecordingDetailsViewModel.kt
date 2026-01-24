package com.f0x1d.logfox.feature.recordings.presentation.details

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RecordingDetailsViewModel @Inject constructor(
    reducer: RecordingDetailsReducer,
    effectHandler: RecordingDetailsEffectHandler,
    dateTimeFormatter: DateTimeFormatter,
) : BaseStoreViewModel<RecordingDetailsState, RecordingDetailsCommand, RecordingDetailsSideEffect>(
    initialState = RecordingDetailsState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffects = listOf(RecordingDetailsSideEffect.LoadRecording),
),
    DateTimeFormatter by dateTimeFormatter
