package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.core.tea.ViewStateMapper
import com.f0x1d.logfox.feature.crashes.api.model.AppCrashesCount
import com.f0x1d.logfox.feature.crashes.presentation.common.model.toPresentationModel
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import javax.inject.Inject

internal class AppCrashesViewStateMapper @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) : ViewStateMapper<AppCrashesState, AppCrashesViewState> {
    override fun map(state: AppCrashesState) = AppCrashesViewState(
        packageName = state.packageName,
        appName = state.appName,
        crashes = state.crashes.map { it.toPresentationModel(it.formattedDate()) },
    )

    private fun AppCrashesCount.formattedDate() =
        "${dateTimeFormatter.formatDate(lastCrash.dateAndTime)} ${dateTimeFormatter.formatTime(lastCrash.dateAndTime)}"
}
