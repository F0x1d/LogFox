package com.f0x1d.logfox.feature.recordings.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.feature.recordings.core.controller.RecordingController
import com.f0x1d.logfox.feature.recordings.core.controller.RecordingState
import com.f0x1d.logfox.feature.recordings.core.repository.RecordingsRepository
import com.f0x1d.logfox.strings.Strings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordingsViewModel @Inject constructor(
    private val recordingsRepository: RecordingsRepository,
    private val recordingController: RecordingController,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel(application) {

    companion object {
        const val EVENT_TYPE_RECORDING_SAVED = "recording_saved"
    }

    val recordings = recordingsRepository.getAllAsFlow()
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

    val recordingState = recordingController.recordingState

    fun toggleStartStop() = viewModelScope.launch {
        if (recordingState.value == RecordingState.IDLE)
            recordingController.record()
        else
            recordingController.end().also {
                sendEvent(EVENT_TYPE_RECORDING_SAVED, it ?: return@also)
            }
    }

    fun togglePauseResume() = viewModelScope.launch {
        if (recordingState.value == RecordingState.PAUSED)
            recordingController.resume()
        else
            recordingController.pause()
    }

    fun clearRecordings() = viewModelScope.launch {
        recordingsRepository.clear()
    }

    fun saveAll() = viewModelScope.launch {
        snackbar(Strings.saving_logs)
        recordingsRepository.saveAll().also {
            sendEvent(EVENT_TYPE_RECORDING_SAVED, it)
        }
    }

    fun delete(logRecording: LogRecording) = viewModelScope.launch {
        recordingsRepository.delete(logRecording)
    }
}
