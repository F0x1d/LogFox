package com.f0x1d.logfox.feature.logging.presentation.extended

import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.logging.api.domain.FormatLogLineUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetSelectedLogLinesFlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LogsExtendedCopyEffectHandler @Inject constructor(
    private val getSelectedLogLinesFlowUseCase: GetSelectedLogLinesFlowUseCase,
    private val formatLogLineUseCase: FormatLogLineUseCase,
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
                            formatLogLineUseCase(line)
                        }
                    }.flowOn(defaultDispatcher)
                    .collect { text ->
                        onCommand(LogsExtendedCopyCommand.TextLoaded(text))
                    }
            }
        }
    }
}
