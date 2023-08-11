package com.f0x1d.logfox.viewmodel.recordings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.repository.logging.RecordingsRepository
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.io.File

class RecordingViewModel @AssistedInject constructor(
    @Assisted recordingId: Long,
    application: Application,
    private val database: AppDatabase,
    private val recordingsRepository: RecordingsRepository
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
        ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
            recording.value?.apply {
                File(file).inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    fun updateTitle(title: String) = recording.value?.apply {
        recordingsRepository.updateTitle(this, title)
    }
}

@AssistedFactory
interface RecordingViewModelAssistedFactory {
    fun create(recordingId: Long): RecordingViewModel
}