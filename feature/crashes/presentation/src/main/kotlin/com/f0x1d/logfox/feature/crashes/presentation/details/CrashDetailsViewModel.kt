package com.f0x1d.logfox.feature.crashes.presentation.details

import android.net.Uri
import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.database.model.AppCrash
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.preferences.domain.GetUseSeparateNotificationsChannelsForCrashesUseCase
import com.f0x1d.logfox.feature.preferences.domain.GetWrapCrashLogLinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CrashDetailsViewModel @Inject constructor(
    reducer: CrashDetailsReducer,
    effectHandler: CrashDetailsEffectHandler,
    blacklistEffectHandler: CrashDetailsBlacklistEffectHandler,
    getWrapCrashLogLinesUseCase: GetWrapCrashLogLinesUseCase,
    getUseSeparateNotificationsChannelsForCrashesUseCase: GetUseSeparateNotificationsChannelsForCrashesUseCase,
    dateTimeFormatter: DateTimeFormatter,
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
),
    DateTimeFormatter by dateTimeFormatter {

    fun exportCrashToZip(uri: Uri) {
        send(CrashDetailsCommand.ExportCrashToZip(uri))
    }

    fun changeBlacklist(appCrash: AppCrash) {
        send(CrashDetailsCommand.ChangeBlacklist(appCrash))
    }

    fun deleteCrash(appCrash: AppCrash) {
        send(CrashDetailsCommand.DeleteCrash(appCrash))
    }
}
