package com.f0x1d.logfox.feature.logging.presentation.extended

import com.f0x1d.logfox.core.tea.ViewStateMapper
import javax.inject.Inject

internal class LogsExtendedCopyViewStateMapper @Inject constructor() : ViewStateMapper<LogsExtendedCopyState, LogsExtendedCopyViewState> {
    override fun map(state: LogsExtendedCopyState): LogsExtendedCopyViewState = LogsExtendedCopyViewState(
        text = state.text,
    )
}
