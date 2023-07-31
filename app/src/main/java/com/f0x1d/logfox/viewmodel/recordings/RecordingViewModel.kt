package com.f0x1d.logfox.viewmodel.recordings

import android.app.Application
import android.net.Uri
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.LogRecording
import com.f0x1d.logfox.repository.logging.RecordingsRepository
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import java.io.File

class RecordingViewModel @AssistedInject constructor(
    @Assisted recordingId: Long,
    application: Application,
    database: AppDatabase,
    private val recordingsRepository: RecordingsRepository
): BaseSameFlowProxyViewModel<LogRecording>(application, database.logRecordingDao().get(recordingId)) {

    fun exportFile(uri: Uri) = launchCatching(Dispatchers.IO) {
        ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
            data.value?.apply {
                File(file).inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    fun updateTitle(title: String) = data.value?.apply {
        recordingsRepository.updateTitle(this, title)
    }
}

@AssistedFactory
interface RecordingViewModelAssistedFactory {
    fun create(recordingId: Long): RecordingViewModel
}