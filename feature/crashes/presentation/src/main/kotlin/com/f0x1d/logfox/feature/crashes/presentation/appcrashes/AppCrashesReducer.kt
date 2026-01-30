package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import com.f0x1d.logfox.feature.crashes.api.model.AppCrashesCount
import com.f0x1d.logfox.feature.crashes.presentation.common.model.toPresentationModel
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import javax.inject.Inject

internal class AppCrashesReducer @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) : Reducer<AppCrashesState, AppCrashesCommand, AppCrashesSideEffect> {

    override fun reduce(
        state: AppCrashesState,
        command: AppCrashesCommand,
    ): ReduceResult<AppCrashesState, AppCrashesSideEffect> = when (command) {
        is AppCrashesCommand.CrashesLoaded -> state.copy(
            crashes = command.crashes.map { it.toPresentationModel(it.formattedDate()) },
        ).noSideEffects()

        is AppCrashesCommand.DeleteCrash -> state.withSideEffects(
            AppCrashesSideEffect.DeleteCrash(command.item.lastCrashId),
        )

        is AppCrashesCommand.CrashClicked -> state.withSideEffects(
            AppCrashesSideEffect.NavigateToCrashDetails(command.crashId),
        )
    }

    private fun AppCrashesCount.formattedDate() =
        "${dateTimeFormatter.formatDate(lastCrash.dateAndTime)} ${dateTimeFormatter.formatTime(lastCrash.dateAndTime)}"
}
