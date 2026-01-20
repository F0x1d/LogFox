package com.f0x1d.logfox.feature.filters.presentation.edit

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
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
                including = command.filter.including,
                enabled = command.filter.enabled,
                enabledLogLevels = enabledLogLevels,
                uid = command.filter.uid,
                pid = command.filter.pid,
                tid = command.filter.tid,
                packageName = command.filter.packageName,
                tag = command.filter.tag,
                content = command.filter.content,
            ).noSideEffects()
        }

        is EditFilterCommand.UpdateUid -> {
            state.copy(uid = command.uid).noSideEffects()
        }

        is EditFilterCommand.UpdatePid -> {
            state.copy(pid = command.pid).noSideEffects()
        }

        is EditFilterCommand.UpdateTid -> {
            state.copy(tid = command.tid).noSideEffects()
        }

        is EditFilterCommand.UpdatePackageName -> {
            state.copy(packageName = command.packageName).noSideEffects()
        }

        is EditFilterCommand.UpdateTag -> {
            state.copy(tag = command.tag).noSideEffects()
        }

        is EditFilterCommand.UpdateContent -> {
            state.copy(content = command.content).noSideEffects()
        }

        is EditFilterCommand.ToggleIncluding -> {
            state.copy(including = !state.including).noSideEffects()
        }

        is EditFilterCommand.ToggleEnabled -> {
            state.copy(enabled = !state.enabled).noSideEffects()
        }

        is EditFilterCommand.FilterLevel -> {
            val newEnabledLogLevels = state.enabledLogLevels.toMutableList().apply {
                this[command.which] = command.filtering
            }
            state.copy(enabledLogLevels = newEnabledLogLevels).noSideEffects()
        }

        is EditFilterCommand.Save -> {
            state.withSideEffects(
                EditFilterSideEffect.SaveFilter(
                    filter = state.filter,
                    including = state.including,
                    enabled = state.enabled,
                    enabledLogLevels = state.enabledLogLevels.toEnabledLogLevels(),
                    uid = state.uid,
                    pid = state.pid,
                    tid = state.tid,
                    packageName = state.packageName,
                    tag = state.tag,
                    content = state.content,
                ),
            )
        }

        is EditFilterCommand.Export -> {
            state.withSideEffects(
                EditFilterSideEffect.ExportFilter(
                    uri = command.uri,
                    filter = state.filter,
                ),
            )
        }

        is EditFilterCommand.AppSelected -> {
            state.copy(packageName = command.packageName)
                .withSideEffects(EditFilterSideEffect.UpdatePackageNameField(command.packageName))
        }
    }

    private fun List<Boolean>.toEnabledLogLevels(): List<LogLevel> = mapIndexedNotNull {
            index,
            value,
        ->
        if (value) LogLevel.entries[index] else null
    }
}
