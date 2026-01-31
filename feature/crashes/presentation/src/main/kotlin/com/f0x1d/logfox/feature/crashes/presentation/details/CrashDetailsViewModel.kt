package com.f0x1d.logfox.feature.crashes.presentation.details

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetUseSeparateNotificationsChannelsForCrashesUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetWrapCrashLogLinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CrashDetailsViewModel @Inject constructor(
    reducer: CrashDetailsReducer,
    effectHandler: CrashDetailsEffectHandler,
    blacklistEffectHandler: CrashDetailsBlacklistEffectHandler,
    viewStateMapper: CrashDetailsViewStateMapper,
    getWrapCrashLogLinesUseCase: GetWrapCrashLogLinesUseCase,
    getUseSeparateNotificationsChannelsForCrashesUseCase: GetUseSeparateNotificationsChannelsForCrashesUseCase,
) : BaseStoreViewModel<CrashDetailsViewState, CrashDetailsState, CrashDetailsCommand, CrashDetailsSideEffect>(
    initialState = CrashDetailsState(
        crash = null,
        crashLog = null,
        blacklisted = null,
        wrapCrashLogLines = getWrapCrashLogLinesUseCase(),
        useSeparateNotificationsChannelsForCrashes = getUseSeparateNotificationsChannelsForCrashesUseCase(),
        searchQuery = "",
        searchMatchRanges = emptyList(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler, blacklistEffectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(
        CrashDetailsSideEffect.LoadCrash,
        CrashDetailsSideEffect.ObservePreferences,
    ),
)
