package com.f0x1d.logfox.feature.crashes.presentation.details

import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.crashes.api.domain.CheckAppDisabledUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.DeleteCrashUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.ExportCrashToFileUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.ExportCrashToZipUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.GetCrashByIdFlowUseCase
import com.f0x1d.logfox.feature.crashes.presentation.details.di.CrashId
import com.f0x1d.logfox.feature.preferences.domain.crashes.GetUseSeparateNotificationsChannelsForCrashesFlowUseCase
import com.f0x1d.logfox.feature.preferences.domain.crashes.GetWrapCrashLogLinesFlowUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class CrashDetailsEffectHandler @Inject constructor(
    @CrashId private val crashId: Long,
    private val getCrashByIdFlowUseCase: GetCrashByIdFlowUseCase,
    private val deleteCrashUseCase: DeleteCrashUseCase,
    private val checkAppDisabledUseCase: CheckAppDisabledUseCase,
    private val exportCrashToFileUseCase: ExportCrashToFileUseCase,
    private val exportCrashToZipUseCase: ExportCrashToZipUseCase,
    private val getWrapCrashLogLinesFlowUseCase: GetWrapCrashLogLinesFlowUseCase,
    private val getUseSeparateNotificationsChannelsForCrashesFlowUseCase: GetUseSeparateNotificationsChannelsForCrashesFlowUseCase,
) : EffectHandler<CrashDetailsSideEffect, CrashDetailsCommand> {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun handle(
        effect: CrashDetailsSideEffect,
        onCommand: suspend (CrashDetailsCommand) -> Unit,
    ) {
        when (effect) {
            is CrashDetailsSideEffect.LoadCrash -> {
                getCrashByIdFlowUseCase(crashId)
                    .map {
                        when (it) {
                            null -> {
                                null
                            }

                            else -> {
                                runCatching {
                                    it to it.logFile?.readText()
                                }.getOrNull()
                            }
                        }
                    }
                    .collect { value ->
                        value?.let { (crash, crashLog) ->
                            onCommand(CrashDetailsCommand.CrashLoaded(crash, crashLog))
                        }
                    }
            }

            is CrashDetailsSideEffect.ObservePreferences -> {
                combine(
                    getWrapCrashLogLinesFlowUseCase(),
                    getUseSeparateNotificationsChannelsForCrashesFlowUseCase(),
                ) { wrapCrashLogLines, useSeparateNotificationsChannelsForCrashes ->
                    CrashDetailsCommand.PreferencesUpdated(
                        wrapCrashLogLines = wrapCrashLogLines,
                        useSeparateNotificationsChannelsForCrashes = useSeparateNotificationsChannelsForCrashes,
                    )
                }.collect { command ->
                    onCommand(command)
                }
            }

            is CrashDetailsSideEffect.ExportCrashToZip -> {
                exportCrashToZipUseCase(effect.uri, effect.appCrash, effect.crashLog)
            }

            is CrashDetailsSideEffect.ExportCrashToFile -> {
                exportCrashToFileUseCase(effect.uri, effect.crashLog.orEmpty())
            }

            is CrashDetailsSideEffect.ChangeBlacklist -> {
                checkAppDisabledUseCase(effect.appCrash.packageName)
            }

            is CrashDetailsSideEffect.DeleteCrash -> {
                deleteCrashUseCase(effect.appCrash.id)
            }

            // UI side effects - handled by Fragment
            is CrashDetailsSideEffect.OpenAppInfo -> Unit
            is CrashDetailsSideEffect.OpenNotificationSettings -> Unit
            is CrashDetailsSideEffect.ConfirmBlacklist -> Unit
            is CrashDetailsSideEffect.ConfirmDelete -> Unit
            is CrashDetailsSideEffect.CopyText -> Unit
            is CrashDetailsSideEffect.ShareCrashLog -> Unit
            is CrashDetailsSideEffect.Close -> Unit
            is CrashDetailsSideEffect.LaunchFileExportPicker -> Unit
            is CrashDetailsSideEffect.LaunchZipExportPicker -> Unit
        }
    }
}
