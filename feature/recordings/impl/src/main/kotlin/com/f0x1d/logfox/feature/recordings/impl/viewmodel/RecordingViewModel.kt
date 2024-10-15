package com.f0x1d.logfox.feature.recordings.impl.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.io.exportToZip
import com.f0x1d.logfox.arch.io.putZipEntry
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.feature.recordings.api.repository.RecordingsRepository
import com.f0x1d.logfox.feature.recordings.impl.di.RecordingId
import com.f0x1d.logfox.model.deviceData
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    @RecordingId val recordingId: Long,
    private val recordingsRepository: RecordingsRepository,
    private val appPreferences: AppPreferences,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    dateTimeFormatter: DateTimeFormatter,
    application: Application
): BaseViewModel(application), DateTimeFormatter by dateTimeFormatter {

    val recording = recordingsRepository.getByIdAsFlow(recordingId)
        .distinctUntilChanged()
        .onEach { recording ->
            currentTitle.update { recording?.title }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    val currentTitle = MutableStateFlow<String?>(null)

    private val titleUpdateMutex = Mutex()

    fun exportFile(uri: Uri) = launchCatching(ioDispatcher) {
        val recording = recording.value ?: return@launchCatching

        ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
            recording.file.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    fun exportZipFile(uri: Uri) = launchCatching(ioDispatcher) {
        val recording = recording.value ?: return@launchCatching

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
            recording.value?.let {
                recordingsRepository.updateTitle(it, title)
            }
        }
    }
}
