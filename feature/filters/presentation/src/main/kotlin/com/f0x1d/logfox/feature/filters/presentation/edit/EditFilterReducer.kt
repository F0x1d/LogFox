package com.f0x1d.logfox.feature.filters.presentation.edit

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import com.f0x1d.logfox.feature.filters.api.model.MatchData
import com.f0x1d.logfox.feature.filters.presentation.edit.di.EditFilterArgs
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import javax.inject.Inject

internal class EditFilterReducer @Inject constructor(
    private val args: EditFilterArgs,
) : Reducer<EditFilterState, EditFilterCommand, EditFilterSideEffect> {

    override fun reduce(
        state: EditFilterState,
        command: EditFilterCommand,
    ): ReduceResult<EditFilterState, EditFilterSideEffect> = when (command) {
        is EditFilterCommand.Load -> {
            state.withSideEffects(EditFilterSideEffect.LoadFilter(args.filterId))
        }

        is EditFilterCommand.FilterLoaded -> {
            val enabledLogLevels = MutableList(LogLevel.entries.size) { false }
            val allowedLevels = command.filter.allowedLevels.map { it.ordinal }
            for (i in enabledLogLevels.indices) {
                enabledLogLevels[i] = allowedLevels.contains(i)
            }

            state.copy(
                filter = command.filter,
                name = command.filter.name,
                including = command.filter.including,
                enabled = command.filter.enabled,
                enabledLogLevels = enabledLogLevels,
                uid = command.filter.uid,
                pid = command.filter.pid,
                tid = command.filter.tid,
                packageName = command.filter.packageName,
                tag = command.filter.tag.value,
                tagMatchMode = command.filter.tag.matchMode,
                content = command.filter.content.value,
                contentMatchMode = command.filter.content.matchMode,
                isDirty = false,
            ).noSideEffects()
        }

        is EditFilterCommand.UpdateName -> {
            state.copy(name = command.name, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.UpdateUid -> {
            state.copy(uid = command.uid, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.UpdatePid -> {
            state.copy(pid = command.pid, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.UpdateTid -> {
            state.copy(tid = command.tid, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.UpdatePackageName -> {
            state.copy(packageName = command.packageName, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.UpdateTag -> {
            state.copy(tag = command.tag, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.SetTagMatchMode -> {
            state.copy(tagMatchMode = command.matchMode, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.UpdateContent -> {
            state.copy(content = command.content, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.SetContentMatchMode -> {
            state.copy(contentMatchMode = command.matchMode, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.ToggleIncluding -> {
            state.copy(including = !state.including, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.ToggleEnabled -> {
            state.copy(enabled = !state.enabled, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.FilterLevel -> {
            val newEnabledLogLevels = state.enabledLogLevels.toMutableList().apply {
                this[command.which] = command.filtering
            }
            state.copy(enabledLogLevels = newEnabledLogLevels, isDirty = true).noSideEffects()
        }

        is EditFilterCommand.Save -> {
            val tag = MatchData(value = state.tag, matchMode = state.tagMatchMode)
            val content = MatchData(value = state.content, matchMode = state.contentMatchMode)
            // Refuse to save an invalid regex; the UI also blocks the FAB, this is defense in depth.
            if (tag.isInvalidRegex || content.isInvalidRegex) {
                state.noSideEffects()
            } else {
                state.withSideEffects(
                    EditFilterSideEffect.SaveFilter(
                        filter = state.filter,
                        name = state.name,
                        including = state.including,
                        enabled = state.enabled,
                        enabledLogLevels = state.enabledLogLevels.toEnabledLogLevels(),
                        uid = state.uid,
                        pid = state.pid,
                        tid = state.tid,
                        packageName = state.packageName,
                        tag = tag,
                        content = content,
                    ),
                    EditFilterSideEffect.Close,
                )
            }
        }

        is EditFilterCommand.Export -> {
            state.withSideEffects(
                EditFilterSideEffect.ExportFilter(
                    uri = command.uri,
                    filter = state.filter,
                ),
            )
        }

        is EditFilterCommand.SelectApp -> {
            state.withSideEffects(EditFilterSideEffect.NavigateToAppPicker)
        }

        is EditFilterCommand.AttemptClose -> {
            state.withSideEffects(
                if (state.isDirty) {
                    EditFilterSideEffect.ConfirmDiscard
                } else {
                    EditFilterSideEffect.Close
                },
            )
        }

        is EditFilterCommand.AttemptCloseConfirmed -> {
            state.withSideEffects(EditFilterSideEffect.Close)
        }
    }

    private fun List<Boolean>.toEnabledLogLevels(): List<LogLevel> = mapIndexedNotNull {
            index,
            value,
        ->
        if (value) LogLevel.entries[index] else null
    }
}
