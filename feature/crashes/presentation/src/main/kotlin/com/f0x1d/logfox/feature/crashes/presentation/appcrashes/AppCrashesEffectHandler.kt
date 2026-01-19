package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.crashes.api.domain.DeleteCrashUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.GetAllCrashesFlowUseCase
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.di.PackageName
import com.f0x1d.logfox.feature.database.model.AppCrashesCount
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AppCrashesEffectHandler @Inject constructor(
    @PackageName private val packageName: String,
    private val getAllCrashesFlowUseCase: GetAllCrashesFlowUseCase,
    private val deleteCrashUseCase: DeleteCrashUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : EffectHandler<AppCrashesSideEffect, AppCrashesCommand> {

    override suspend fun handle(
        effect: AppCrashesSideEffect,
        onCommand: suspend (AppCrashesCommand) -> Unit,
    ) {
        when (effect) {
            is AppCrashesSideEffect.LoadCrashes -> {
                getAllCrashesFlowUseCase()
                    .map { crashes ->
                        crashes.filter { crash ->
                            crash.packageName == packageName
                        }.map {
                            AppCrashesCount(it)
                        }
                    }
                    .flowOn(defaultDispatcher)
                    .collect { crashes ->
                        onCommand(AppCrashesCommand.CrashesLoaded(crashes))
                    }
            }

            is AppCrashesSideEffect.DeleteCrash -> {
                deleteCrashUseCase(effect.appCrash)
            }
        }
    }
}
