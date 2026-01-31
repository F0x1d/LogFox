package com.f0x1d.logfox.feature.recordings.presentation.details

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import javax.inject.Inject

internal class RecordingDetailsReducer @Inject constructor() : Reducer<RecordingDetailsState, RecordingDetailsCommand, RecordingDetailsSideEffect> {

    override fun reduce(
        state: RecordingDetailsState,
        command: RecordingDetailsCommand,
    ): ReduceResult<RecordingDetailsState, RecordingDetailsSideEffect> = when (command) {
        is RecordingDetailsCommand.Load -> {
            state.withSideEffects(RecordingDetailsSideEffect.LoadRecording)
        }

        is RecordingDetailsCommand.RecordingLoaded -> {
            state.copy(
                recording = command.recording,
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
            val recording = state.recording
            if (recording != null) {
                state.copy(currentTitle = command.title)
                    .withSideEffects(
                        RecordingDetailsSideEffect.UpdateTitle(command.title, recording.id),
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
