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
            val item = state.recordingItem
            if (item != null) {
                state.withSideEffects(RecordingDetailsSideEffect.ExportFile(command.uri, item.file))
            } else {
                state.noSideEffects()
            }
        }

        is RecordingDetailsCommand.ExportZipFile -> {
            val item = state.recordingItem
            if (item != null) {
                state.withSideEffects(
                    RecordingDetailsSideEffect.ExportZipFile(command.uri, item.file),
                )
            } else {
                state.noSideEffects()
            }
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
            val item = state.recordingItem
            if (item != null) {
                val filename = "${dateTimeFormatter.formatForExport(item.dateAndTime)}.log"
                state.withSideEffects(
                    RecordingDetailsSideEffect.LaunchFileExportPicker(filename = filename),
                )
            } else {
                state.noSideEffects()
            }
        }

        is RecordingDetailsCommand.ExportZipClicked -> {
            val item = state.recordingItem
            if (item != null) {
                val filename = "${dateTimeFormatter.formatForExport(item.dateAndTime)}.zip"
                state.withSideEffects(
                    RecordingDetailsSideEffect.LaunchZipExportPicker(filename = filename),
                )
            } else {
                state.noSideEffects()
            }
        }

        is RecordingDetailsCommand.ShareRecording -> {
            val item = state.recordingItem
            if (item != null) {
                state.withSideEffects(RecordingDetailsSideEffect.ShareFile(item.file))
            } else {
                state.noSideEffects()
            }
        }
    }
}
