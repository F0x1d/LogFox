package com.f0x1d.logfox.viewmodel.recordings

import android.app.Application
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.repository.logging.RecordingState
import com.f0x1d.logfox.repository.logging.RecordingsRepository
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class RecordingsViewModel @Inject constructor(
    application: Application,
    private val database: AppDatabase,
    private val loggingRepository: LoggingRepository,
    private val recordingsRepository: RecordingsRepository
): BaseViewModel(application) {

    companion object {
        const val EVENT_TYPE_RECORDING_SAVED = "recording_saved"
    }

    val recordings = database.logRecordingDao().getAllAsFlow()
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
        .asLiveData()

    val recordingStateData = recordingsRepository.recordingStateFlow.asLiveData()

    val loggingServiceOrRecordingActive get() = loggingRepository.serviceRunningFlow.value || recordingsRepository.recordingStateFlow.value != RecordingState.IDLE

    fun toggleStartStop() {
        if (recordingsRepository.recordingStateFlow.value == RecordingState.IDLE)
            recordingsRepository.record()
        else recordingsRepository.end {
            sendEvent(EVENT_TYPE_RECORDING_SAVED, it)
        }
    }

    fun togglePauseResume() {
        if (recordingsRepository.recordingStateFlow.value == RecordingState.PAUSED)
            recordingsRepository.resume()
        else
            recordingsRepository.pause()
    }

    fun clearRecordings() = recordingsRepository.clear()

    fun delete(logRecording: LogRecording) = recordingsRepository.delete(logRecording)
}