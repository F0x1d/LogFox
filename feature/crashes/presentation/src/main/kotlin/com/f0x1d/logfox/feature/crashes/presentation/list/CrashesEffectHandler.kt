package com.f0x1d.logfox.feature.crashes.presentation.list

import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.crashes.api.domain.CheckAppDisabledUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.ClearAllCrashesUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.DeleteAllCrashesByPackageNameUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.DeleteCrashUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.GetAllCrashesFlowUseCase
import com.f0x1d.logfox.feature.database.model.AppCrashesCount
import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class CrashesEffectHandler
    @Inject
    constructor(
        private val getAllCrashesFlowUseCase: GetAllCrashesFlowUseCase,
        private val deleteAllCrashesByPackageNameUseCase: DeleteAllCrashesByPackageNameUseCase,
        private val deleteCrashUseCase: DeleteCrashUseCase,
        private val clearAllCrashesUseCase: ClearAllCrashesUseCase,
        private val checkAppDisabledUseCase: CheckAppDisabledUseCase,
        private val crashesSettingsRepository: CrashesSettingsRepository,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    ) : EffectHandler<CrashesSideEffect, CrashesCommand> {
        override suspend fun handle(
            effect: CrashesSideEffect,
            onCommand: suspend (CrashesCommand) -> Unit,
        ) {
            when (effect) {
                is CrashesSideEffect.LoadCrashes -> {
                    combine(
                        getAllCrashesFlowUseCase(),
                        crashesSettingsRepository.crashesSortType(),
                        crashesSettingsRepository.crashesSortReversedOrder(),
                    ) { crashes, sortType, sortInReversedOrder ->
                        val groupedCrashes = crashes.groupBy { it.packageName }

                        val appCrashes =
                            groupedCrashes
                                .map {
                                    AppCrashesCount(
                                        lastCrash = it.value.first(),
                                        count = it.value.size,
                                    )
                                }.let(sortType.sorter)
                                .let { result ->
                                    if (sortInReversedOrder) {
                                        result.asReversed()
                                    } else {
                                        result
                                    }
                                }

                        Triple(appCrashes, sortType, sortInReversedOrder)
                    }.distinctUntilChanged()
                        .flowOn(defaultDispatcher)
                        .collect { (crashes, sortType, sortInReversedOrder) ->
                            onCommand(
                                CrashesCommand.CrashesLoaded(
                                    crashes = crashes,
                                    sortType = sortType,
                                    sortInReversedOrder = sortInReversedOrder,
                                ),
                            )
                        }
                }

                is CrashesSideEffect.UpdateSortPreferences -> {
                    crashesSettingsRepository.crashesSortType().set(effect.sortType)
                    crashesSettingsRepository.crashesSortReversedOrder().set(effect.sortInReversedOrder)
                }

                is CrashesSideEffect.DeleteCrashesByPackageName -> {
                    deleteAllCrashesByPackageNameUseCase(effect.appCrash)
                }

                is CrashesSideEffect.DeleteCrash -> {
                    deleteCrashUseCase(effect.appCrash)
                }

                is CrashesSideEffect.ClearAllCrashes -> {
                    clearAllCrashesUseCase()
                }

                is CrashesSideEffect.CheckAppDisabled -> {
                    when (effect.disabled) {
                        null -> checkAppDisabledUseCase(effect.packageName)
                        else -> checkAppDisabledUseCase(effect.packageName, effect.disabled)
                    }
                }
            }
        }
    }
