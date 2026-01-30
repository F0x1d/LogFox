package com.f0x1d.logfox.feature.logging.presentation.list

import android.net.Uri
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.logging.presentation.list.model.LogLineItem

sealed interface LogsCommand {
    data class LogsLoaded(
        val logs: List<FormattedLogLine>,
        val query: String?,
        val filters: List<UserFilter>,
    ) : LogsCommand {
        data class FormattedLogLine(
            val logLine: LogLine,
            val displayText: CharSequence,
        )
    }

    data class PreferencesUpdated(
        val resumeLoggingWithBottomTouch: Boolean,
        val logsTextSize: Float,
        val logsExpanded: Boolean,
        val logsFormat: ShowLogValues,
    ) : LogsCommand

    data class SelectLine(val logLineItem: LogLineItem, val selected: Boolean) : LogsCommand

    data object SelectAll : LogsCommand

    data object SelectedToRecording : LogsCommand

    data class ExportSelectedTo(val uri: Uri) : LogsCommand

    data object SwitchState : LogsCommand

    data object Pause : LogsCommand

    data object Resume : LogsCommand

    data object ClearSelection : LogsCommand

    data class CopyLog(val logLineItem: LogLineItem) : LogsCommand

    data object CopySelectedLogs : LogsCommand

    data class CopyFormattedText(val text: String) : LogsCommand

    data object ToolbarClicked : LogsCommand

    data class CreateFilterFromLog(val logLineItem: LogLineItem) : LogsCommand

    data object OpenSearch : LogsCommand

    data object OpenFiltersScreen : LogsCommand

    data object OpenExtendedCopy : LogsCommand

    data object ExportSelectedClicked : LogsCommand

    data object ClearLogs : LogsCommand

    data object RestartLogging : LogsCommand

    data object KillService : LogsCommand
}
