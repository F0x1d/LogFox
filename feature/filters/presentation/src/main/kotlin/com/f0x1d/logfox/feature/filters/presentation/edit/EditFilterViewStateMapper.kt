package com.f0x1d.logfox.feature.filters.presentation.edit

import com.f0x1d.logfox.core.tea.ViewStateMapper
import com.f0x1d.logfox.feature.filters.api.model.MatchData
import com.f0x1d.logfox.feature.filters.api.model.MatchMode
import javax.inject.Inject

internal class EditFilterViewStateMapper @Inject constructor() : ViewStateMapper<EditFilterState, EditFilterViewState> {
    override fun map(state: EditFilterState): EditFilterViewState {
        // Pattern validity is derived here (not stored on the state) so the view state carries it as
        // plain fields. MatchData owns the regex-compile logic, so we reuse it for both fields.
        val tagRegexError = regexError(state.tag, state.tagMatchMode)
        val contentRegexError = regexError(state.content, state.contentMatchMode)

        return EditFilterViewState(
            filter = state.filter,
            name = state.name?.takeIf { it.isNotBlank() },
            including = state.including,
            enabled = state.enabled,
            enabledLogLevels = state.enabledLogLevels,
            uid = state.uid,
            pid = state.pid,
            tid = state.tid,
            packageName = state.packageName,
            tag = state.tag,
            tagMatchMode = state.tagMatchMode,
            tagRegexError = tagRegexError,
            content = state.content,
            contentMatchMode = state.contentMatchMode,
            contentRegexError = contentRegexError,
            canSave = !tagRegexError && !contentRegexError,
            isDirty = state.isDirty,
        )
    }

    private fun regexError(value: String?, matchMode: MatchMode) =
        MatchData(value = value, matchMode = matchMode).isInvalidRegex
}
