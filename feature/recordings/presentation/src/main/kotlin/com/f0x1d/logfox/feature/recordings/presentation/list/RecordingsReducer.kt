package com.f0x1d.logfox.feature.recordings.presentation.list

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import javax.inject.Inject

internal class RecordingsReducer @Inject constructor() : Reducer<RecordingsState, RecordingsCommand, RecordingsSideEffect> {

    override fun reduce(
        state: RecordingsState,
        command: RecordingsCommand,
    ): ReduceResult<RecordingsState, RecordingsSideEffect> = when (command) {
        is RecordingsCommand.Load -> {
            state.withSideEffects(RecordingsSideEffect.LoadRecordings)
        }

        is RecordingsCommand.RecordingsLoaded -> {
            state.copy(
                recordings = command.recordings,
                recordingState = command.recordingState,
            ).noSideEffects()
        }

        is RecordingsCommand.ToggleStartStop -> {
            state.withSideEffects(RecordingsSideEffect.ToggleStartStop)
        }

        is RecordingsCommand.TogglePauseResume -> {
            state.withSideEffects(RecordingsSideEffect.TogglePauseResume)
        }

        is RecordingsCommand.ClearRecordings -> {
            state.withSideEffects(RecordingsSideEffect.ClearRecordings)
        }

        is RecordingsCommand.SaveAll -> {
            state.withSideEffects(RecordingsSideEffect.SaveAll)
        }

        is RecordingsCommand.Delete -> {
            state.withSideEffects(RecordingsSideEffect.DeleteRecording(command.recordingId))
        }

        is RecordingsCommand.RecordingEnded -> {
            val recording = command.recording
            val recordingId = recording?.id
            if (recordingId != null) {
                state.withSideEffects(RecordingsSideEffect.OpenRecording(recordingId))
            } else {
                state.noSideEffects()
            }
        }

        is RecordingsCommand.SaveAllCompleted -> {
            state.withSideEffects(RecordingsSideEffect.OpenRecording(command.recording.id))
        }

        is RecordingsCommand.ShowSavingSnackbar -> {
            state.withSideEffects(RecordingsSideEffect.ShowSnackbar(command.text))
        }

        is RecordingsCommand.OpenRecordingDetails -> {
            state.withSideEffects(RecordingsSideEffect.OpenRecording(command.recordingId))
        }
    }
}
