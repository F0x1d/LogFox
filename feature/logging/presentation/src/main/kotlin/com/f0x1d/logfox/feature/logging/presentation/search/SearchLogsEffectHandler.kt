package com.f0x1d.logfox.feature.logging.presentation.search

import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.logging.api.domain.GetQueryFlowUseCase
import com.f0x1d.logfox.feature.logging.api.domain.UpdateQueryUseCase
import javax.inject.Inject

internal class SearchLogsEffectHandler @Inject constructor(
    private val getQueryFlowUseCase: GetQueryFlowUseCase,
    private val updateQueryUseCase: UpdateQueryUseCase,
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

            // UI side effects - handled by Fragment
            is SearchLogsSideEffect.Dismiss -> Unit
        }
    }
}
