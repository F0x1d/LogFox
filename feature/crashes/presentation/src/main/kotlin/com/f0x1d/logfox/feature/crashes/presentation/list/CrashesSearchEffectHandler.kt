package com.f0x1d.logfox.feature.crashes.presentation.list

import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.crashes.api.domain.GetAllCrashesFlowUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.GetCrashesSearchQueryFlowUseCase
import com.f0x1d.logfox.feature.database.model.AppCrashesCount
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class CrashesSearchEffectHandler @Inject constructor(
    private val getAllCrashesFlowUseCase: GetAllCrashesFlowUseCase,
    private val getCrashesSearchQueryFlowUseCase: GetCrashesSearchQueryFlowUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : EffectHandler<CrashesSideEffect, CrashesCommand> {

    override suspend fun handle(
        effect: CrashesSideEffect,
        onCommand: suspend (CrashesCommand) -> Unit,
    ) {
        when (effect) {
            is CrashesSideEffect.LoadCrashes -> {
                combine(
                    getAllCrashesFlowUseCase().distinctUntilChanged(),
                    getCrashesSearchQueryFlowUseCase(),
                ) { crashes, query -> crashes to query }
                    .map { (crashes, query) ->
                        crashes.filter { crash ->
                            crash.packageName.contains(query, ignoreCase = true)
                                    || crash.appName?.contains(query, ignoreCase = true) == true
                        }.map { AppCrashesCount(it) }
                    }
                    .distinctUntilChanged()
                    .flowOn(defaultDispatcher)
                    .collect { searchedCrashes ->
                        onCommand(CrashesCommand.SearchedCrashesLoaded(searchedCrashes))
                    }
            }

            else -> {
                // Handled by other effect handlers
            }
        }
    }
}
