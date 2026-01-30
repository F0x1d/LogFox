package com.f0x1d.logfox.feature.recordings.presentation.details

import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.recordings.api.domain.ExportRecordingFileUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.ExportRecordingZipUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.GetRecordingByIdFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.UpdateRecordingTitleUseCase
import com.f0x1d.logfox.feature.recordings.presentation.di.RecordingId
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

internal class RecordingDetailsEffectHandler @Inject constructor(
    @RecordingId private val recordingId: Long,
    private val getRecordingByIdFlowUseCase: GetRecordingByIdFlowUseCase,
    private val updateRecordingTitleUseCase: UpdateRecordingTitleUseCase,
    private val exportRecordingFileUseCase: ExportRecordingFileUseCase,
    private val exportRecordingZipUseCase: ExportRecordingZipUseCase,
    private val dateTimeFormatter: DateTimeFormatter,
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

            is RecordingDetailsSideEffect.PrepareFileExport -> {
                val recording = getRecordingByIdFlowUseCase(recordingId).firstOrNull()
                    ?: return
                val filename = "${dateTimeFormatter.formatForExport(recording.dateAndTime)}.log"
                onCommand(RecordingDetailsCommand.FileExportPickerReady(filename))
            }

            is RecordingDetailsSideEffect.PrepareZipExport -> {
                val recording = getRecordingByIdFlowUseCase(recordingId).firstOrNull()
                    ?: return
                val filename = "${dateTimeFormatter.formatForExport(recording.dateAndTime)}.zip"
                onCommand(RecordingDetailsCommand.ZipExportPickerReady(filename))
            }

            is RecordingDetailsSideEffect.PrepareShare -> {
                val recording = getRecordingByIdFlowUseCase(recordingId).firstOrNull()
                    ?: return
                onCommand(RecordingDetailsCommand.ShareFileReady(recording.file))
            }

            is RecordingDetailsSideEffect.ExportFile -> {
                exportRecordingFileUseCase(recordingId, effect.uri)
            }

            is RecordingDetailsSideEffect.ExportZipFile -> {
                exportRecordingZipUseCase(recordingId, effect.uri)
            }

            is RecordingDetailsSideEffect.UpdateTitle -> {
                titleUpdateMutex.withLock {
                    updateRecordingTitleUseCase(effect.recordingId, effect.title)
                }
            }

            // UI side effects - handled by Fragment
            is RecordingDetailsSideEffect.LaunchFileExportPicker -> Unit
            is RecordingDetailsSideEffect.LaunchZipExportPicker -> Unit
            is RecordingDetailsSideEffect.ShareFile -> Unit
        }
    }
}
