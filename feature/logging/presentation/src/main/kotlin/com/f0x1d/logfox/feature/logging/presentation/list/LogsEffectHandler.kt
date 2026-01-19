package com.f0x1d.logfox.feature.logging.presentation.list

import android.content.Context
import android.net.Uri
import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.filters.api.domain.GetAllEnabledFiltersFlowUseCase
import com.f0x1d.logfox.feature.filters.api.model.filterAndSearch
import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetLogsFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateSelectedLogLinesUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.model.ShowLogValues
import com.f0x1d.logfox.feature.logging.presentation.di.FileUri
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.recordings.api.domain.CreateRecordingFromLinesUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LogsEffectHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    @FileUri private val fileUri: Uri?,
    private val getLogsFlowUseCase: GetLogsFlowUseCase,
    private val getQueryFlowUseCase: GetQueryFlowUseCase,
    private val getAllEnabledFiltersFlowUseCase: GetAllEnabledFiltersFlowUseCase,
    private val updateSelectedLogLinesUseCase: UpdateSelectedLogLinesUseCase,
    private val createRecordingFromLinesUseCase: CreateRecordingFromLinesUseCase,
    private val logsSettingsRepository: LogsSettingsRepository,
    private val formatLogLineUseCase: FormatLogLineUseCase,
    private val dateTimeFormatter: DateTimeFormatter,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : EffectHandler<LogsSideEffect, LogsCommand> {

    private val viewingFile = fileUri != null
    private val pausedFlow = MutableStateFlow(false)

    override suspend fun handle(effect: LogsSideEffect, onCommand: suspend (LogsCommand) -> Unit) {
        when (effect) {
            is LogsSideEffect.LoadLogs -> {
                combine(
                    fileUri?.readFileContentsAsFlow(
                        context = context,
                        logsDisplayLimit = logsSettingsRepository.logsDisplayLimit().value,
                    ) ?: getLogsFlowUseCase(),
                    getAllEnabledFiltersFlowUseCase(),
                    getQueryFlowUseCase(),
                    if (!viewingFile) pausedFlow else flowOf(false),
                ) { logs, filters, query, paused ->
                    LogsData(
                        logs = logs,
                        filters = filters,
                        query = query,
                        paused = paused,
                    )
                }.scan(LogsData()) { accumulator, data ->
                    when {
                        !data.paused || data.logs.isEmpty() -> {
                            data
                        }

                        data.query != accumulator.query ||
                            data.filters != accumulator.filters -> {
                            data.copy(
                                logs = accumulator.logs,
                            )
                        }

                        else -> {
                            data.copy(
                                logs = accumulator.logs,
                                passing = false,
                            )
                        }
                    }
                }.filter { data ->
                    data.passing
                }.mapNotNull { data ->
                    data.copy(
                        logs = data.logs.filterAndSearch(
                            filters = data.filters,
                            query = data.query,
                        ),
                    )
                }.flowOn(defaultDispatcher)
                    .collect { data ->
                        onCommand(
                            LogsCommand.LogsLoaded(
                                logs = data.logs,
                                query = data.query,
                                filters = data.filters,
                            ),
                        )
                    }
            }

            is LogsSideEffect.ObservePreferences -> {
                combine(
                    logsSettingsRepository.resumeLoggingWithBottomTouch(),
                    logsSettingsRepository.logsTextSize(),
                    logsSettingsRepository.logsExpanded(),
                    logsSettingsRepository.showLogDate(),
                    logsSettingsRepository.showLogTime(),
                ) { resumeLoggingWithBottomTouch, logsTextSize, logsExpanded, showDate, showTime ->
                    PreferencesDataPart1(
                        resumeLoggingWithBottomTouch = resumeLoggingWithBottomTouch,
                        logsTextSize = logsTextSize.toFloat(),
                        logsExpanded = logsExpanded,
                        showLogDate = showDate,
                        showLogTime = showTime,
                    )
                }.combine(
                    combine(
                        logsSettingsRepository.showLogUid(),
                        logsSettingsRepository.showLogPid(),
                        logsSettingsRepository.showLogTid(),
                        logsSettingsRepository.showLogPackage(),
                        logsSettingsRepository.showLogTag(),
                    ) { showUid, showPid, showTid, showPackage, showTag ->
                        PreferencesDataPart2(
                            showLogUid = showUid,
                            showLogPid = showPid,
                            showLogTid = showTid,
                            showLogPackage = showPackage,
                            showLogTag = showTag,
                        )
                    },
                ) { part1, part2 ->
                    part1 to part2
                }.combine(
                    logsSettingsRepository.showLogContent(),
                ) { (part1, part2), showContent ->
                    LogsCommand.PreferencesUpdated(
                        resumeLoggingWithBottomTouch = part1.resumeLoggingWithBottomTouch,
                        logsTextSize = part1.logsTextSize,
                        logsExpanded = part1.logsExpanded,
                        logsFormat = ShowLogValues(
                            date = part1.showLogDate,
                            time = part1.showLogTime,
                            uid = part2.showLogUid,
                            pid = part2.showLogPid,
                            tid = part2.showLogTid,
                            packageName = part2.showLogPackage,
                            tag = part2.showLogTag,
                            content = showContent,
                        ),
                    )
                }.collect { command ->
                    onCommand(command)
                }
            }

            is LogsSideEffect.PauseStateChanged -> {
                pausedFlow.value = effect.paused
            }

            is LogsSideEffect.UpdateSelectedLogLines -> {
                updateSelectedLogLinesUseCase(selectedLines = effect.selectedLines)
            }

            is LogsSideEffect.CreateRecordingFromLines -> {
                withContext(defaultDispatcher) {
                    runCatching {
                        createRecordingFromLinesUseCase(lines = effect.lines)
                    }
                }
            }

            is LogsSideEffect.ExportLogsTo -> {
                withContext(ioDispatcher) {
                    runCatching {
                        val content = effect.lines.joinToString("\n") { line ->
                            formatLogLineUseCase(
                                logLine = line,
                                formatDate = dateTimeFormatter::formatDate,
                                formatTime = dateTimeFormatter::formatTime,
                            )
                        }
                        context.contentResolver.openOutputStream(effect.uri)?.use {
                            it.write(content.encodeToByteArray())
                        }
                    }
                }
            }

            // UI side effects - handled by Fragment
            is LogsSideEffect.NavigateToRecordings -> {
                Unit
            }
        }
    }

    private data class LogsData(
        val logs: List<LogLine> = emptyList(),
        val filters: List<UserFilter> = emptyList(),
        val query: String? = null,
        val paused: Boolean = false,
        val passing: Boolean = true,
    )

    private data class PreferencesDataPart1(
        val resumeLoggingWithBottomTouch: Boolean,
        val logsTextSize: Float,
        val logsExpanded: Boolean,
        val showLogDate: Boolean,
        val showLogTime: Boolean,
    )

    private data class PreferencesDataPart2(
        val showLogUid: Boolean,
        val showLogPid: Boolean,
        val showLogTid: Boolean,
        val showLogPackage: Boolean,
        val showLogTag: Boolean,
    )
}
