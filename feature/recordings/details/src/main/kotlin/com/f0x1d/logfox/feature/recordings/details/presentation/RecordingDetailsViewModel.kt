package com.f0x1d.logfox.feature.recordings.details.presentation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.io.exportToZip
import com.f0x1d.logfox.arch.io.putZipEntry
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.feature.recordings.api.data.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.details.di.RecordingId
import com.f0x1d.logfox.model.deviceData
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class RecordingDetailsViewModel @Inject constructor(
    @RecordingId val recordingId: Long,
    private val recordingsRepository: RecordingsRepository,
    private val appPreferences: AppPreferences,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    dateTimeFormatter: DateTimeFormatter,
    application: Application
): BaseViewModel<RecordingDetailsState, RecordingDetailsAction>(
    initialStateProvider = { RecordingDetailsState() },
    application = application,
), DateTimeFormatter by dateTimeFormatter {
    var currentTitle: String? = null
        private set

    private val titleUpdateMutex = Mutex()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            recordingsRepository.getByIdAsFlow(recordingId)
                .distinctUntilChanged()
                .take(1)
                .collect { recording ->
                    currentTitle = recording?.title
                    reduce { copy(recording = recording) }
                }
        }
    }

    fun exportFile(uri: Uri) = launchCatching(ioDispatcher) {
        val recording = currentState.recording ?: return@launchCatching

        ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
            recording.file.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    fun exportZipFile(uri: Uri) = launchCatching(ioDispatcher) {
        val recording = currentState.recording ?: return@launchCatching

        ctx.contentResolver.openOutputStream(uri)?.use {
            it.exportToZip {
                if (appPreferences.includeDeviceInfoInArchives) putZipEntry(
                    "device.txt",
                    deviceData.encodeToByteArray()
                )

                putZipEntry(
                    name = "recorded.log",
                    file = recording.file
                )
            }
        }
    }

    fun updateTitle(title: String) = launchCatching {
        titleUpdateMutex.withLock {
            currentTitle = title

            currentState.recording?.let {
                recordingsRepository.updateTitle(it, title)
            }
        }
    }
}
