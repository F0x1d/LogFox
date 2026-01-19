package com.f0x1d.logfox.feature.crashes.presentation.details

import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.crashes.api.domain.GetCrashByIdFlowUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.IsAppDisabledFlowUseCase
import com.f0x1d.logfox.feature.crashes.presentation.details.di.CrashId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class CrashDetailsBlacklistEffectHandler @Inject constructor(
    @CrashId private val crashId: Long,
    private val getCrashByIdFlowUseCase: GetCrashByIdFlowUseCase,
    private val isAppDisabledFlowUseCase: IsAppDisabledFlowUseCase,
) : EffectHandler<CrashDetailsSideEffect, CrashDetailsCommand> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun handle(
        effect: CrashDetailsSideEffect,
        onCommand: suspend (CrashDetailsCommand) -> Unit,
    ) {
        when (effect) {
            is CrashDetailsSideEffect.LoadCrash -> {
                getCrashByIdFlowUseCase(crashId)
                    .flatMapLatest { crash ->
                        crash?.let {
                            isAppDisabledFlowUseCase(it.packageName)
                        } ?: flowOf(null)
                    }
                    .collect { blacklisted ->
                        onCommand(CrashDetailsCommand.BlacklistStatusLoaded(blacklisted))
                    }
            }

            else -> {
                // Handled by other effect handlers
            }
        }
    }
}
