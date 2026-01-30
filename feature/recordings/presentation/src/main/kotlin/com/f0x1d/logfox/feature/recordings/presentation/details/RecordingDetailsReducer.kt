package com.f0x1d.logfox.feature.recordings.presentation.details

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.recordings.presentation.model.toPresentationModel
import javax.inject.Inject

internal class RecordingDetailsReducer @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) : Reducer<RecordingDetailsState, RecordingDetailsCommand, RecordingDetailsSideEffect> {

    override fun reduce(
        state: RecordingDetailsState,
        command: RecordingDetailsCommand,
    ): ReduceResult<RecordingDetailsState, RecordingDetailsSideEffect> = when (command) {
        is RecordingDetailsCommand.Load -> {
            state.withSideEffects(RecordingDetailsSideEffect.LoadRecording)
        }

        is RecordingDetailsCommand.RecordingLoaded -> {
            val item = command.recording?.let {
                it.toPresentationModel(
                    formattedDate = "${dateTimeFormatter.formatDate(it.dateAndTime)} ${dateTimeFormatter.formatTime(it.dateAndTime)}",
                )
            }
            state.copy(
                recordingItem = item,
                currentTitle = command.recording?.title,
            ).noSideEffects()
        }

        is RecordingDetailsCommand.ExportFile -> {
            state.withSideEffects(RecordingDetailsSideEffect.ExportFile(command.uri))
        }

        is RecordingDetailsCommand.ExportZipFile -> {
            state.withSideEffects(RecordingDetailsSideEffect.ExportZipFile(command.uri))
        }

        is RecordingDetailsCommand.UpdateTitle -> {
            val item = state.recordingItem
            if (item != null) {
                state.copy(currentTitle = command.title)
                    .withSideEffects(
                        RecordingDetailsSideEffect.UpdateTitle(command.title, item.recordingId),
                    )
            } else {
                state.noSideEffects()
            }
        }

        is RecordingDetailsCommand.ExportFileClicked -> {
            state.withSideEffects(RecordingDetailsSideEffect.PrepareFileExport)
        }

        is RecordingDetailsCommand.ExportZipClicked -> {
            state.withSideEffects(RecordingDetailsSideEffect.PrepareZipExport)
        }

        is RecordingDetailsCommand.ShareRecording -> {
            state.withSideEffects(RecordingDetailsSideEffect.PrepareShare)
        }

        is RecordingDetailsCommand.FileExportPickerReady -> {
            state.withSideEffects(
                RecordingDetailsSideEffect.LaunchFileExportPicker(filename = command.filename),
            )
        }

        is RecordingDetailsCommand.ZipExportPickerReady -> {
            state.withSideEffects(
                RecordingDetailsSideEffect.LaunchZipExportPicker(filename = command.filename),
            )
        }

        is RecordingDetailsCommand.ShareFileReady -> {
            state.withSideEffects(RecordingDetailsSideEffect.ShareFile(command.file))
        }
    }
}
