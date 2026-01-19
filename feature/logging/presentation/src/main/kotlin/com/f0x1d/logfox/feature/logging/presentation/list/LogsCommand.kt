package com.f0x1d.logfox.feature.logging.presentation.list

import android.net.Uri
import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.logging.api.model.LogLine

sealed interface LogsCommand {
    data object Load : LogsCommand

    data class LogsLoaded(
        val logs: List<LogLine>,
        val query: String?,
        val filters: List<UserFilter>,
    ) : LogsCommand

    data class SelectLine(
        val logLine: LogLine,
        val selected: Boolean,
    ) : LogsCommand

    data object SelectAll : LogsCommand

    data object SelectedToRecording : LogsCommand

    data class ExportSelectedTo(val uri: Uri) : LogsCommand

    data object SwitchState : LogsCommand

    data object Pause : LogsCommand

    data object Resume : LogsCommand

    data object ClearSelection : LogsCommand
}
