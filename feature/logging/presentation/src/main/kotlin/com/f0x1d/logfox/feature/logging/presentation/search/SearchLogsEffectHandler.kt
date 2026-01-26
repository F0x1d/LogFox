package com.f0x1d.logfox.feature.logging.presentation.search

import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.logging.api.domain.GetCaseSensitiveFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateCaseSensitiveUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateQueryUseCase
import javax.inject.Inject

internal class SearchLogsEffectHandler @Inject constructor(
    private val getQueryFlowUseCase: GetQueryFlowUseCase,
    private val updateQueryUseCase: UpdateQueryUseCase,
    private val getCaseSensitiveFlowUseCase: GetCaseSensitiveFlowUseCase,
    private val updateCaseSensitiveUseCase: UpdateCaseSensitiveUseCase,
) : EffectHandler<SearchLogsSideEffect, SearchLogsCommand> {

    override suspend fun handle(
        effect: SearchLogsSideEffect,
        onCommand: suspend (SearchLogsCommand) -> Unit,
    ) {
        when (effect) {
            is SearchLogsSideEffect.LoadQuery -> {
                getQueryFlowUseCase().collect { query ->
                    onCommand(SearchLogsCommand.QueryLoaded(query))
                }
            }

            is SearchLogsSideEffect.SaveQuery -> {
                updateQueryUseCase(effect.query)
            }

            is SearchLogsSideEffect.LoadCaseSensitive -> {
                getCaseSensitiveFlowUseCase().collect { caseSensitive ->
                    onCommand(SearchLogsCommand.CaseSensitiveLoaded(caseSensitive))
                }
            }

            is SearchLogsSideEffect.SaveCaseSensitive -> {
                updateCaseSensitiveUseCase(effect.caseSensitive)
            }

            // UI side effects - handled by Fragment
            is SearchLogsSideEffect.Dismiss -> Unit
        }
    }
}
