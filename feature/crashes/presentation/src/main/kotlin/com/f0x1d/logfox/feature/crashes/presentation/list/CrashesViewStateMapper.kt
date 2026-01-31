package com.f0x1d.logfox.feature.crashes.presentation.list

import com.f0x1d.logfox.core.tea.ViewStateMapper
import com.f0x1d.logfox.feature.crashes.api.model.AppCrashesCount
import com.f0x1d.logfox.feature.crashes.presentation.common.model.toPresentationModel
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import javax.inject.Inject

internal class CrashesViewStateMapper @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) : ViewStateMapper<CrashesState, CrashesViewState> {
    override fun map(state: CrashesState) = CrashesViewState(
        crashes = state.crashes.map { it.toPresentationModel(it.formattedDate()) },
        searchedCrashes = state.searchedCrashes.map { it.toPresentationModel(it.formattedDate()) },
        currentSort = state.currentSort,
        sortInReversedOrder = state.sortInReversedOrder,
        query = state.query,
    )

    private fun AppCrashesCount.formattedDate() =
        "${dateTimeFormatter.formatDate(lastCrash.dateAndTime)} ${dateTimeFormatter.formatTime(lastCrash.dateAndTime)}"
}
