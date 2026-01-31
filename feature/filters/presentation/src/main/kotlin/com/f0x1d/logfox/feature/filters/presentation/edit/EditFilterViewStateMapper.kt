package com.f0x1d.logfox.feature.filters.presentation.edit

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class EditFilterViewStateMapper @Inject constructor() : ViewStateMapper<EditFilterState, EditFilterViewState> {
    override fun map(state: EditFilterState) = EditFilterViewState(
        filter = state.filter,
        including = state.including,
        enabled = state.enabled,
        enabledLogLevels = state.enabledLogLevels,
        uid = state.uid,
        pid = state.pid,
        tid = state.tid,
        packageName = state.packageName,
        tag = state.tag,
        content = state.content,
    )
}
