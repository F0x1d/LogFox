package com.f0x1d.logfox.feature.crashes.presentation.details

import android.net.Uri
import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.database.model.AppCrash
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CrashDetailsViewModel
@Inject
constructor(
    reducer: CrashDetailsReducer,
    effectHandler: CrashDetailsEffectHandler,
    blacklistEffectHandler: CrashDetailsBlacklistEffectHandler,
    private val crashesSettingsRepository: CrashesSettingsRepository,
    dateTimeFormatter: DateTimeFormatter,
) : BaseStoreViewModel<CrashDetailsState, CrashDetailsCommand, CrashDetailsSideEffect>(
    initialState = CrashDetailsState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler, blacklistEffectHandler),
    initialSideEffect = CrashDetailsSideEffect.LoadCrash,
),
    DateTimeFormatter by dateTimeFormatter {
    val wrapCrashLogLines get() = crashesSettingsRepository.wrapCrashLogLines().value
    val useSeparateNotificationsChannelsForCrashes get() = crashesSettingsRepository.useSeparateNotificationsChannelsForCrashes().value

    val currentState: CrashDetailsState get() = state.value

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
