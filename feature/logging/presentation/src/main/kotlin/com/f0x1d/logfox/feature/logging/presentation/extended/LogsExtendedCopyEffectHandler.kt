package com.f0x1d.logfox.feature.logging.presentation.extended

import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.api.domain.GetSelectedLogLinesFlowUseCase
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LogsExtendedCopyEffectHandler
    @Inject
    constructor(
        private val getSelectedLogLinesFlowUseCase: GetSelectedLogLinesFlowUseCase,
        private val logsSettingsRepository: LogsSettingsRepository,
        private val dateTimeFormatter: DateTimeFormatter,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    ) : EffectHandler<LogsExtendedCopySideEffect, LogsExtendedCopyCommand> {
        override suspend fun handle(
            effect: LogsExtendedCopySideEffect,
            onCommand: suspend (LogsExtendedCopyCommand) -> Unit,
        ) {
            when (effect) {
                is LogsExtendedCopySideEffect.LoadSelectedLines -> {
                    getSelectedLogLinesFlowUseCase()
                        .map { lines ->
                            lines.joinToString("\n") { line ->
                                logsSettingsRepository.originalOf(
                                    logLine = line,
                                    formatDate = dateTimeFormatter::formatDate,
                                    formatTime = dateTimeFormatter::formatTime,
                                )
                            }
                        }.flowOn(defaultDispatcher)
                        .collect { text ->
                            onCommand(LogsExtendedCopyCommand.TextLoaded(text))
                        }
                }
            }
        }
    }
