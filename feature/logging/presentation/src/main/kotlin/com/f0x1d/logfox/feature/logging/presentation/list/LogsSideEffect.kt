package com.f0x1d.logfox.feature.logging.presentation.list

import android.net.Uri
import com.f0x1d.logfox.feature.logging.api.model.LogLine

sealed interface LogsSideEffect {
    // Business logic side effects - handled by EffectHandler
    data object LoadLogs : LogsSideEffect

    data object ObservePreferences : LogsSideEffect

    data class PauseStateChanged(val paused: Boolean) : LogsSideEffect

    data class UpdateSelectedLogLines(val selectedLines: List<LogLine>) : LogsSideEffect

    data class CreateRecordingFromLines(val lines: List<LogLine>) : LogsSideEffect

    data class ExportLogsTo(val uri: Uri, val lines: List<LogLine>) : LogsSideEffect

    data class FormatAndCopyLog(val logLine: LogLine) : LogsSideEffect

    data class FormatAndCopyLogs(val logLines: List<LogLine>) : LogsSideEffect

    // UI side effects - handled by Fragment
    data object NavigateToRecordings : LogsSideEffect

    data class CopyText(val text: String) : LogsSideEffect
}
