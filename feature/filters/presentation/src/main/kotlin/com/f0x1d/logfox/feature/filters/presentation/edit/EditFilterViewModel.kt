package com.f0x1d.logfox.feature.filters.presentation.edit

import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.feature.apps.picker.api.AppsPickerResultHandler
import com.f0x1d.logfox.feature.apps.picker.api.InstalledApp
import com.f0x1d.logfox.feature.filters.presentation.edit.di.EditFilterArgs
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EditFilterViewModel @Inject constructor(
    args: EditFilterArgs,
    reducer: EditFilterReducer,
    effectHandler: EditFilterEffectHandler,
    viewStateMapper: EditFilterViewStateMapper,
) : BaseStoreViewModel<EditFilterViewState, EditFilterState, EditFilterCommand, EditFilterSideEffect>(
    initialState = args.toInitialState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = buildList {
        if (args.hasValidFilterId) {
            add(EditFilterSideEffect.LoadFilter(args.filterId))
        }
    },
),
    AppsPickerResultHandler {

    override fun onAppSelected(app: InstalledApp): Boolean {
        send(EditFilterCommand.UpdatePackageName(app.packageName))
        return true
    }
}

private fun EditFilterArgs.toInitialState(): EditFilterState {
    if (!hasInitialData) return EditFilterState(
        filter = null,
        including = true,
        enabled = true,
        enabledLogLevels = List(LogLevel.entries.size) { false },
        uid = null,
        pid = null,
        tid = null,
        packageName = null,
        tag = null,
        content = null,
    )

    val enabledLogLevels = MutableList(LogLevel.entries.size) { false }
    if (level != null && level >= 0 && level < LogLevel.entries.size) {
        enabledLogLevels[level] = true
    }

    return EditFilterState(
        filter = null,
        including = true,
        enabled = true,
        enabledLogLevels = enabledLogLevels,
        uid = uid,
        pid = pid,
        tid = tid,
        packageName = packageName,
        tag = tag,
        content = content,
    )
}
