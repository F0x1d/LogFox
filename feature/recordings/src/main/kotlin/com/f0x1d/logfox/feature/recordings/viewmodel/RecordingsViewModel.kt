package com.f0x1d.logfox.feature.recordings.viewmodel

import android.app.Application
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.arch.viewmodel.Event
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.feature.recordings.core.controller.RecordingController
import com.f0x1d.logfox.feature.recordings.core.controller.RecordingState
import com.f0x1d.logfox.feature.recordings.core.repository.RecordingsRepository
import com.f0x1d.logfox.strings.Strings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@HiltViewModel
class RecordingsViewModel @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
    private val recordingController: RecordingController,
    application: Application,
): BaseViewModel(application) {

    val recordings = recordingsRepository.getAllAsFlow()
        .distinctUntilChanged()

    val recordingState = recordingController.recordingState

    fun toggleStartStop() = launchCatching {
        if (recordingState.value == RecordingState.IDLE)
            recordingController.record()
        else
            recordingController.end().also {
                sendEvent(OpenRecording(it))
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
        snackbar(Strings.saving_logs)
        recordingsRepository.saveAll().also {
            sendEvent(OpenRecording(it))
        }
    }

    fun delete(logRecording: LogRecording) = launchCatching {
        recordingsRepository.delete(logRecording)
    }
}

data class OpenRecording(
    val recording: LogRecording?,
) : Event
