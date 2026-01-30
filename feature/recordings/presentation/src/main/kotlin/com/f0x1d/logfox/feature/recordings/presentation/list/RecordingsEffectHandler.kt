package com.f0x1d.logfox.feature.recordings.presentation.list

import android.content.Context
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.api.domain.ClearAllRecordingsUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.DeleteRecordingUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.EndRecordingUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.GetAllRecordingsFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.GetRecordingStateFlowUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.PauseRecordingUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.ResumeRecordingUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.SaveAllRecordingsUseCase
import com.f0x1d.logfox.feature.recordings.api.domain.StartRecordingUseCase
import com.f0x1d.logfox.feature.strings.Strings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

internal class RecordingsEffectHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getAllRecordingsFlowUseCase: GetAllRecordingsFlowUseCase,
    private val clearAllRecordingsUseCase: ClearAllRecordingsUseCase,
    private val saveAllRecordingsUseCase: SaveAllRecordingsUseCase,
    private val deleteRecordingUseCase: DeleteRecordingUseCase,
    private val getRecordingStateFlowUseCase: GetRecordingStateFlowUseCase,
    private val startRecordingUseCase: StartRecordingUseCase,
    private val pauseRecordingUseCase: PauseRecordingUseCase,
    private val resumeRecordingUseCase: ResumeRecordingUseCase,
    private val endRecordingUseCase: EndRecordingUseCase,
) : EffectHandler<RecordingsSideEffect, RecordingsCommand> {

    override suspend fun handle(
        effect: RecordingsSideEffect,
        onCommand: suspend (RecordingsCommand) -> Unit,
    ) {
        when (effect) {
            is RecordingsSideEffect.LoadRecordings -> {
                combine(
                    getAllRecordingsFlowUseCase().distinctUntilChanged(),
                    getRecordingStateFlowUseCase(),
                ) { recordings, recordingState ->
                    RecordingsCommand.RecordingsLoaded(
                        recordings = recordings,
                        recordingState = recordingState,
                    )
                }.collect { command ->
                    onCommand(command)
                }
            }

            is RecordingsSideEffect.ToggleStartStop -> {
                if (getRecordingStateFlowUseCase().value == RecordingState.IDLE) {
                    startRecordingUseCase()
                } else {
                    val recording = endRecordingUseCase()
                    onCommand(RecordingsCommand.RecordingEnded(recording))
                }
            }

            is RecordingsSideEffect.TogglePauseResume -> {
                if (getRecordingStateFlowUseCase().value == RecordingState.PAUSED) {
                    resumeRecordingUseCase()
                } else {
                    pauseRecordingUseCase()
                }
            }

            is RecordingsSideEffect.ClearRecordings -> {
                clearAllRecordingsUseCase()
            }

            is RecordingsSideEffect.SaveAll -> {
                onCommand(
                    RecordingsCommand.ShowSavingSnackbar(context.getString(Strings.saving_logs)),
                )
                val recording = saveAllRecordingsUseCase()
                onCommand(RecordingsCommand.SaveAllCompleted(recording))
            }

            is RecordingsSideEffect.DeleteRecording -> {
                deleteRecordingUseCase(effect.recordingId)
            }

            // UI side effects - handled by Fragment, ignored here
            is RecordingsSideEffect.ShowSnackbar -> Unit

            is RecordingsSideEffect.OpenRecording -> Unit
        }
    }
}
