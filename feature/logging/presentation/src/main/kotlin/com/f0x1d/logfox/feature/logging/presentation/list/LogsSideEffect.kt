package com.f0x1d.logfox.feature.logging.presentation.list

import android.net.Uri
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import com.f0x1d.logfox.feature.logging.api.model.LogLine

internal sealed interface LogsSideEffect {
    // Business logic side effects - handled by EffectHandler
    data object LoadLogs : LogsSideEffect

    data object ObservePreferences : LogsSideEffect

    data class SyncSelectedLines(val lines: List<LogLine>) : LogsSideEffect

    data class CreateRecordingFromLines(val lines: List<LogLine>) : LogsSideEffect

    data class ExportLogsTo(val uri: Uri, val lines: List<LogLine>) : LogsSideEffect

    data class FormatAndCopyLog(val logLine: LogLine) : LogsSideEffect

    data class FormatAndCopyLogs(val lines: List<LogLine>) : LogsSideEffect

    // UI side effects - handled by Fragment
    data object NavigateToRecordings : LogsSideEffect

    data object OpenFilters : LogsSideEffect

    data class OpenEditFilter(val filterId: Long) : LogsSideEffect

    data class OpenEditFilterFromLogLine(
        val uid: String,
        val pid: String,
        val tid: String,
        val packageName: String?,
        val tag: String,
        val content: String,
        val level: LogLevel,
    ) : LogsSideEffect

    data class CopyText(val text: String) : LogsSideEffect

    data object NavigateToSearch : LogsSideEffect

    data object NavigateToExtendedCopy : LogsSideEffect

    data class LaunchExportPicker(val filename: String) : LogsSideEffect

    data object ClearLogs : LogsSideEffect

    data object RestartLogging : LogsSideEffect

    data object KillService : LogsSideEffect
}
