package com.f0x1d.logfox.viewmodel.crashes

import android.app.Application
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.repository.logging.CrashesRepository
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

class CrashDetailsViewModel @AssistedInject constructor(
    @Assisted crashId: Long,
    application: Application,
    private val database: AppDatabase,
    private val crashesRepository: CrashesRepository
): BaseViewModel(application) {

    companion object {
        const val EVENT_TYPE_COPY_LINK = "copy_link"
    }

    val crash = database.appCrashDao().get(crashId)
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
        .asLiveData()

    fun deleteCrash(appCrash: AppCrash) = crashesRepository.delete(appCrash)
}

@AssistedFactory
interface CrashDetailsViewModelAssistedFactory {
    fun create(crashId: Long): CrashDetailsViewModel
}