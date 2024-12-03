package com.f0x1d.logfox.feature.recordings.list.presentation

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.feature.recordings.api.data.RecordingController
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.strings.Strings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordingsViewModel @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
    private val recordingController: RecordingController,
    application: Application,
): BaseViewModel<RecordingsState, RecordingsAction>(
    initialStateProvider = { RecordingsState() },
    application = application,
) {
    private val recordingState = recordingController.recordingState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            combine(
                recordingsRepository.getAllAsFlow().distinctUntilChanged(),
                recordingState,
            ) { recordings, recordingState ->
                RecordingsState(
                    recordings = recordings,
                    recordingState = recordingState,
                )
            }.collect {
                reduce { it }
            }
        }
    }

    fun toggleStartStop() = launchCatching {
        if (recordingState.value == RecordingState.IDLE)
            recordingController.record()
        else
            recordingController.end()?.also {
                sendAction(RecordingsAction.OpenRecording(it))
            }
    }

    fun togglePauseResume() = launchCatching {
        if (recordingState.value == RecordingState.PAUSED)
            recordingController.resume()
        else
            recordingController.pause()
    }

    fun clearRecordings() = launchCatching {
        recordingsRepository.clear()
    }

    fun saveAll() = launchCatching {
        sendAction(RecordingsAction.ShowSnackbar(ctx.getString(Strings.saving_logs)))
        recordingsRepository.saveAll().also {
            sendAction(RecordingsAction.OpenRecording(it))
        }
    }

    fun delete(logRecording: LogRecording) = launchCatching {
        recordingsRepository.delete(logRecording)
    }
}
