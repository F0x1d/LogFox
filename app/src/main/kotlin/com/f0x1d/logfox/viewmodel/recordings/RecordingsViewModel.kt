package com.f0x1d.logfox.viewmodel.recordings

import android.app.Application
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.R
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.repository.logging.RecordingState
import com.f0x1d.logfox.repository.logging.RecordingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class RecordingsViewModel @Inject constructor(
    private val database: AppDatabase,
    private val recordingsRepository: RecordingsRepository,
    application: Application
): BaseViewModel(application) {

    companion object {
        const val EVENT_TYPE_RECORDING_SAVED = "recording_saved"
    }

    val recordings = database.logRecordingDao().getAllAsFlow().toLiveData()
    val cachedRecordings = database.logRecordingDao().getAllAsFlow(cached = true).toLiveData()

    val recordingStateData = recordingsRepository.recordingStateFlow.asLiveData()

    fun toggleStartStop() {
        if (recordingsRepository.recordingStateFlow.value == RecordingState.IDLE)
            recordingsRepository.record()
        else recordingsRepository.end {
            sendEvent(EVENT_TYPE_RECORDING_SAVED, it)
        }
    }

    fun togglePauseResume() = if (recordingsRepository.recordingStateFlow.value == RecordingState.PAUSED)
        recordingsRepository.resume()
    else
        recordingsRepository.pause()

    fun clearRecordings() = recordingsRepository.clear()
    fun clearCachedRecordings() = recordingsRepository.clearCached()

    fun saveAll() = recordingsRepository.saveAll {
        sendEvent(EVENT_TYPE_RECORDING_SAVED, it)
    }.also {
        snackbar(R.string.saving_logs)
    }

    fun delete(logRecording: LogRecording) = recordingsRepository.delete(logRecording)

    private fun Flow<List<LogRecording>>.toLiveData() = distinctUntilChanged().flowOn(Dispatchers.IO).asLiveData()
}
