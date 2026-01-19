package com.f0x1d.logfox.feature.recordings.presentation.details

import android.content.Context
import com.f0x1d.logfox.core.context.deviceData
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.io.exportToZip
import com.f0x1d.logfox.core.io.putZipEntry
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.preferences.domain.service.GetIncludeDeviceInfoInArchivesUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.GetRecordingByIdFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.UpdateRecordingTitleUseCase
import com.f0x1d.logfox.feature.recordings.presentation.di.RecordingId
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RecordingDetailsEffectHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    @RecordingId private val recordingId: Long,
    private val getRecordingByIdFlowUseCase: GetRecordingByIdFlowUseCase,
    private val updateRecordingTitleUseCase: UpdateRecordingTitleUseCase,
    private val getIncludeDeviceInfoInArchivesUseCase: GetIncludeDeviceInfoInArchivesUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : EffectHandler<RecordingDetailsSideEffect, RecordingDetailsCommand> {
    private val titleUpdateMutex = Mutex()

    override suspend fun handle(
        effect: RecordingDetailsSideEffect,
        onCommand: suspend (RecordingDetailsCommand) -> Unit,
    ) {
        when (effect) {
            is RecordingDetailsSideEffect.LoadRecording -> {
                getRecordingByIdFlowUseCase(recordingId)
                    .distinctUntilChanged()
                    .take(1)
                    .collect { recording ->
                        onCommand(RecordingDetailsCommand.RecordingLoaded(recording))
                    }
            }

            is RecordingDetailsSideEffect.ExportFile -> {
                withContext(ioDispatcher) {
                    context.contentResolver.openOutputStream(effect.uri)?.use { outputStream ->
                        effect.recording.file.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            }

            is RecordingDetailsSideEffect.ExportZipFile -> {
                withContext(ioDispatcher) {
                    context.contentResolver.openOutputStream(effect.uri)?.use {
                        it.exportToZip {
                            if (getIncludeDeviceInfoInArchivesUseCase()) {
                                putZipEntry(
                                    "device.txt",
                                    deviceData.encodeToByteArray(),
                                )
                            }

                            putZipEntry(
                                name = "recorded.log",
                                file = effect.recording.file,
                            )
                        }
                    }
                }
            }

            is RecordingDetailsSideEffect.UpdateTitle -> {
                titleUpdateMutex.withLock {
                    updateRecordingTitleUseCase(effect.recording, effect.title)
                }
            }
        }
    }
}
