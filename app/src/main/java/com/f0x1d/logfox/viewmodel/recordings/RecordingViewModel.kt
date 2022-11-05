package com.f0x1d.logfox.viewmodel.recordings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.LogRecording
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class RecordingViewModel @AssistedInject constructor(application: Application,
                                                     database: AppDatabase,
                                                     @Assisted private val recordingId: Long): BaseSameFlowProxyViewModel<LogRecording>(
    application,
    database.logRecordingDao().get(recordingId)
) {
    fun exportFile(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        ctx.contentResolver.openOutputStream(uri)?.apply {
            data.value?.apply {
                write(File(file).readBytes())
            }
            close()
        }
    }
}

@AssistedFactory
interface RecordingViewModelAssistedFactory {
    fun create(recordingId: Long): RecordingViewModel
}