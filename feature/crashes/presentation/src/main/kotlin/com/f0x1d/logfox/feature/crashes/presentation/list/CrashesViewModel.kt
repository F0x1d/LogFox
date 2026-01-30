package com.f0x1d.logfox.feature.crashes.presentation.list

import android.content.Context
import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.apps.picker.AppsPickerResultHandler
import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import com.f0x1d.logfox.feature.crashes.api.domain.GetAllDisabledAppsFlowUseCase
import com.f0x1d.logfox.feature.crashes.api.model.DisabledApp
import com.f0x1d.logfox.feature.strings.Strings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class CrashesViewModel @Inject constructor(
    reducer: CrashesReducer,
    effectHandler: CrashesEffectHandler,
    searchEffectHandler: CrashesSearchEffectHandler,
    private val getAllDisabledAppsFlowUseCase: GetAllDisabledAppsFlowUseCase,
) : BaseStoreViewModel<CrashesState, CrashesCommand, CrashesSideEffect>(
    initialState = CrashesState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler, searchEffectHandler),
    initialSideEffects = listOf(CrashesSideEffect.LoadCrashes),
),
    AppsPickerResultHandler {

    // AppsPickerResultHandler implementation
    override val supportsMultiplySelection: Boolean = true

    override val checkedAppPackageNames: Flow<Set<String>> =
        getAllDisabledAppsFlowUseCase().map { apps ->
            apps.map(DisabledApp::packageName).toSet()
        }

    override fun providePickerTopAppBarTitle(context: Context): String = context.getString(Strings.blacklist)

    override fun onAppChecked(app: InstalledApp, checked: Boolean) {
        send(CrashesCommand.CheckAppDisabled(app.packageName, checked))
    }

    override fun onAppSelected(app: InstalledApp): Boolean {
        send(CrashesCommand.CheckAppDisabled(app.packageName))
        return false
    }
}
