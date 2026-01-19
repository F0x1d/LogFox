package com.f0x1d.logfox.feature.filters.presentation.edit

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.apps.picker.AppsPickerResultHandler
import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import com.f0x1d.logfox.feature.filters.presentation.edit.di.FilterId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EditFilterViewModel @Inject constructor(
    @FilterId val filterId: Long?,
    reducer: EditFilterReducer,
    effectHandler: EditFilterEffectHandler,
) : BaseStoreViewModel<EditFilterState, EditFilterCommand, EditFilterSideEffect>(
    initialState = EditFilterState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    initialSideEffect = EditFilterSideEffect.LoadFilter(filterId),
), AppsPickerResultHandler {

    override fun onAppSelected(app: InstalledApp): Boolean {
        send(EditFilterCommand.AppSelected(app.packageName))
        return true
    }
}
