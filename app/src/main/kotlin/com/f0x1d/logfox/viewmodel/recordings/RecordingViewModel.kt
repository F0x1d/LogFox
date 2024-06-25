package com.f0x1d.logfox.viewmodel.recordings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.di.viewmodel.RecordingId
import com.f0x1d.logfox.io.exportToZip
import com.f0x1d.logfox.io.putZipEntry
import com.f0x1d.logfox.model.deviceData
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.repository.logging.RecordingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    @RecordingId val recordingId: Long,
    val dateTimeFormatter: DateTimeFormatter,
    private val database: AppDatabase,
    private val recordingsRepository: RecordingsRepository,
    private val appPreferences: AppPreferences,
    application: Application
): BaseViewModel(application) {

    val recording = database.logRecordingDao().get(recordingId)
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
        .onEach { recording ->
            currentTitle.update { recording?.title }
        }
        .asLiveData()

    val currentTitle = MutableStateFlow<String?>(null)

    fun exportFile(uri: Uri) = launchCatching(Dispatchers.IO) {
        val recording = recording.value ?: return@launchCatching

        ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
            recording.file.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    fun exportZipFile(uri: Uri) = launchCatching(Dispatchers.IO) {
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

    fun updateTitle(title: String) = recording.value?.let {
        recordingsRepository.updateTitle(it, title)
    }
}
