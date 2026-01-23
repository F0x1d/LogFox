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
            val recording = state.recording
            if (recording != null) {
                state.withSideEffects(RecordingDetailsSideEffect.ExportFile(command.uri, recording))
            } else {
                state.noSideEffects()
            }
        }

        is RecordingDetailsCommand.ExportZipFile -> {
            val recording = state.recording
            if (recording != null) {
                state.withSideEffects(
                    RecordingDetailsSideEffect.ExportZipFile(command.uri, recording),
                )
            } else {
                state.noSideEffects()
            }
        }

        is RecordingDetailsCommand.UpdateTitle -> {
            val recording = state.recording
            if (recording != null) {
                state.copy(currentTitle = command.title)
                    .withSideEffects(
                        RecordingDetailsSideEffect.UpdateTitle(command.title, recording),
                    )
            } else {
                state.noSideEffects()
            }
        }

        is RecordingDetailsCommand.ViewRecording -> {
            val recording = state.recording
            if (recording != null) {
                state.withSideEffects(RecordingDetailsSideEffect.NavigateToViewRecording(recording.file))
            } else {
                state.noSideEffects()
            }
        }
    }
}
