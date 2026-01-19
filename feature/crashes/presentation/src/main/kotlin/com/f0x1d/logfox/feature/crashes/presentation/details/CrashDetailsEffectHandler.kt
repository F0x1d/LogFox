package com.f0x1d.logfox.feature.crashes.presentation.details

import android.app.Application
import com.f0x1d.logfox.core.context.deviceData
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.io.exportToZip
import com.f0x1d.logfox.core.io.putZipEntry
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.crashes.api.domain.CheckAppDisabledUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.DeleteCrashUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.GetCrashByIdFlowUseCase
import com.f0x1d.logfox.feature.crashes.api.domain.IsAppDisabledFlowUseCase
import com.f0x1d.logfox.feature.crashes.presentation.details.di.CrashId
import com.f0x1d.logfox.feature.preferences.data.ServiceSettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CrashDetailsEffectHandler
    @Inject
    constructor(
        @CrashId private val crashId: Long,
        private val getCrashByIdFlowUseCase: GetCrashByIdFlowUseCase,
        private val deleteCrashUseCase: DeleteCrashUseCase,
        private val checkAppDisabledUseCase: CheckAppDisabledUseCase,
        private val serviceSettingsRepository: ServiceSettingsRepository,
        @IODispatcher private val ioDispatcher: CoroutineDispatcher,
        private val application: Application,
    ) : EffectHandler<CrashDetailsSideEffect, CrashDetailsCommand> {
        @OptIn(ExperimentalCoroutinesApi::class)
        override suspend fun handle(
            effect: CrashDetailsSideEffect,
            onCommand: suspend (CrashDetailsCommand) -> Unit,
        ) {
            when (effect) {
                is CrashDetailsSideEffect.LoadCrash -> {
                    // Subscribe to crash data
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
                        }.flowOn(ioDispatcher)
                        .collect { value ->
                            value?.let { (crash, crashLog) ->
                                onCommand(CrashDetailsCommand.CrashLoaded(crash, crashLog))
                            }
                        }
                }

                is CrashDetailsSideEffect.ExportCrashToZip -> {
                    withContext(ioDispatcher) {
                        application.contentResolver.openOutputStream(effect.uri)?.use {
                            it.exportToZip {
                                if (serviceSettingsRepository.includeDeviceInfoInArchives) {
                                    putZipEntry(
                                        name = "device.txt",
                                        content = deviceData.encodeToByteArray(),
                                    )
                                }

                                if (effect.crashLog != null) {
                                    putZipEntry(
                                        name = "crash.log",
                                        content = effect.crashLog.encodeToByteArray(),
                                    )
                                }

                                effect.appCrash.logDumpFile?.let { logDumpFile ->
                                    putZipEntry(
                                        name = "dump.log",
                                        file = logDumpFile,
                                    )
                                }
                            }
                        }
                    }
                }

                is CrashDetailsSideEffect.ChangeBlacklist -> {
                    checkAppDisabledUseCase(effect.appCrash.packageName)
                }

                is CrashDetailsSideEffect.DeleteCrash -> {
                    deleteCrashUseCase(effect.appCrash)
                }
            }
        }
    }
