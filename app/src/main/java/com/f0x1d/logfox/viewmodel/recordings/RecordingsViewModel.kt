package com.f0x1d.logfox.viewmodel.recordings

import android.app.Application
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.LogRecording
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.repository.logging.RecordingState
import com.f0x1d.logfox.repository.logging.RecordingsRepository
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecordingsViewModel @Inject constructor(application: Application,
                                              private val loggingRepository: LoggingRepository,
                                              private val recordingsRepository: RecordingsRepository): BaseSameFlowProxyViewModel<List<LogRecording>>(
    application,
    recordingsRepository.recordingsFlow
) {

    companion object {
        const val EVENT_TYPE_RECORDING_SAVED = "recording_saved"
    }

    val recordingStateData = recordingsRepository.recordingStateFlow.asLiveData()

    val loggingServiceOrRecordingActive get() = loggingRepository.serviceRunningFlow.value || recordingsRepository.recordingStateFlow.value != RecordingState.IDLE

    fun toggleStartStop() {
        if (recordingsRepository.recordingStateFlow.value == RecordingState.IDLE)
            recordingsRepository.record()
        else
            recordingsRepository.end {
                sendEvent(EVENT_TYPE_RECORDING_SAVED, it)
            }
    }

    fun togglePauseResume() {
        if (recordingsRepository.recordingStateFlow.value == RecordingState.PAUSED)
            recordingsRepository.resume()
        else
            recordingsRepository.pause()
    }

    fun clearRecordings() = recordingsRepository.clearRecordings()

    fun delete(logRecording: LogRecording) = recordingsRepository.deleteRecording(logRecording)
}