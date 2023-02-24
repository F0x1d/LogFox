package com.f0x1d.logfox.viewmodel.recordings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.LogRecording
import com.f0x1d.logfox.repository.logging.RecordingsRepository
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream

class RecordingViewModel @AssistedInject constructor(
    @Assisted recordingId: Long,
    application: Application,
    database: AppDatabase,
    private val recordingsRepository: RecordingsRepository
): BaseSameFlowProxyViewModel<LogRecording>(application, database.logRecordingDao().get(recordingId)) {

    fun exportFile(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        ctx.contentResolver.openOutputStream(uri)?.also {
            data.value?.apply {
                with(FileInputStream(File(file))) {
                    copyTo(it)

                    close()
                    it.close()
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