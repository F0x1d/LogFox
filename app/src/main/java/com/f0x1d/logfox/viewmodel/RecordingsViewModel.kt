package com.f0x1d.logfox.viewmodel

import android.app.Application
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.LogRecording
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.repository.RecordingState
import com.f0x1d.logfox.repository.RecordsRepository
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecordingsViewModel @Inject constructor(application: Application,
                                              private val recordsRepository: RecordsRepository): BaseSameFlowProxyViewModel<List<LogRecording>>(
    application,
    recordsRepository.recordingsFlow
) {

    companion object {
        const val EVENT_TYPE_RECORDING_SAVED = "recording_saved"
    }

    val recordingStateData = recordsRepository.recordingStateFlow.asLiveData()

    fun toggleStartStop() {
        if (recordsRepository.recordingStateFlow.value == RecordingState.IDLE)
            recordsRepository.record()
        else
            recordsRepository.end {
                eventsData.sendEvent(EVENT_TYPE_RECORDING_SAVED, it)
            }
    }

    fun togglePauseResume() {
        if (recordsRepository.recordingStateFlow.value == RecordingState.PAUSED)
            recordsRepository.record()
        else
            recordsRepository.pause()
    }

    fun clearRecordings() = recordsRepository.clearRecording()
}