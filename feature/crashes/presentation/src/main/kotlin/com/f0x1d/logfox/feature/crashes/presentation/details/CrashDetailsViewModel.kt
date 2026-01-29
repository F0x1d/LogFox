package com.f0x1d.logfox.feature.crashes.presentation.details

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import com.f0x1d.logfox.feature.preferences.domain.crashes.GetUseSeparateNotificationsChannelsForCrashesUseCase
import com.f0x1d.logfox.feature.preferences.domain.crashes.GetWrapCrashLogLinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CrashDetailsViewModel @Inject constructor(
    reducer: CrashDetailsReducer,
    effectHandler: CrashDetailsEffectHandler,
    blacklistEffectHandler: CrashDetailsBlacklistEffectHandler,
    getWrapCrashLogLinesUseCase: GetWrapCrashLogLinesUseCase,
    getUseSeparateNotificationsChannelsForCrashesUseCase: GetUseSeparateNotificationsChannelsForCrashesUseCase,
) : BaseStoreViewModel<CrashDetailsState, CrashDetailsCommand, CrashDetailsSideEffect>(
    initialState = CrashDetailsState(
        wrapCrashLogLines = getWrapCrashLogLinesUseCase(),
        useSeparateNotificationsChannelsForCrashes = getUseSeparateNotificationsChannelsForCrashesUseCase(),
    ),
    reducer = reducer,
    effectHandlers = listOf(effectHandler, blacklistEffectHandler),
    initialSideEffects = listOf(
        CrashDetailsSideEffect.LoadCrash,
        CrashDetailsSideEffect.ObservePreferences,
    ),
) {

    fun changeBlacklist(appCrash: AppCrash) {
        send(CrashDetailsCommand.ChangeBlacklist(appCrash))
    }

    fun deleteCrash(appCrash: AppCrash) {
        send(CrashDetailsCommand.DeleteCrash(appCrash))
    }
}
