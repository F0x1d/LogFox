package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.database.model.AppCrash
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.di.AppName
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.di.PackageName
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AppCrashesViewModel @Inject constructor(
    @PackageName val packageName: String,
    @AppName val appName: String?,
    reducer: AppCrashesReducer,
    effectHandler: AppCrashesEffectHandler,
) : BaseStoreViewModel<AppCrashesState, AppCrashesCommand, AppCrashesSideEffect>(
    initialState = AppCrashesState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffect = AppCrashesSideEffect.LoadCrashes,
) {
    fun deleteCrash(appCrash: AppCrash) {
        send(AppCrashesCommand.DeleteCrash(appCrash))
    }
}
