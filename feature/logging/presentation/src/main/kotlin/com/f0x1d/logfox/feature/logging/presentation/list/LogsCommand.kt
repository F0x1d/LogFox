package com.f0x1d.logfox.feature.logging.presentation.list

import android.net.Uri
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues

internal sealed interface LogsCommand {
    data class LogsLoaded(
        val logs: List<LogLine>,
        val query: String?,
        val caseSensitive: Boolean,
        val filters: List<UserFilter>,
        val showLogValues: ShowLogValues,
    ) : LogsCommand

    data class PreferencesUpdated(
        val resumeLoggingWithBottomTouch: Boolean,
        val textSize: Int,
        val logsExpanded: Boolean,
    ) : LogsCommand

    data class ItemClicked(val logLineId: Long) : LogsCommand

    data class SelectLine(val logLineId: Long, val selected: Boolean) : LogsCommand

    data class SelectAll(val visibleIds: Set<Long>) : LogsCommand

    data object ClearSelection : LogsCommand

    data object SelectedToRecording : LogsCommand

    data class ExportSelectedTo(val uri: Uri) : LogsCommand

    data object ExportSelectedClicked : LogsCommand

    data object SwitchState : LogsCommand

    data object Pause : LogsCommand

    data object Resume : LogsCommand

    data class CopyLog(val logLineId: Long) : LogsCommand

    data object CopySelectedLogs : LogsCommand

    data class CopyFormattedText(val text: String) : LogsCommand

    data object ToolbarClicked : LogsCommand

    data class CreateFilterFromLog(val logLineId: Long) : LogsCommand

    data object OpenSearch : LogsCommand

    data object OpenFiltersScreen : LogsCommand

    data object OpenExtendedCopy : LogsCommand

    data object ClearLogs : LogsCommand

    data object RestartLogging : LogsCommand

    data object KillService : LogsCommand
}
