package com.f0x1d.logfox.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.repository.FoxBinRepository
import com.f0x1d.logfox.repository.logging.CrashesRepository
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers

class CrashDetailsViewModel @AssistedInject constructor(application: Application,
                                                        database: AppDatabase,
                                                        @Assisted crashId: Long,
                                                        private val foxBinRepository: FoxBinRepository,
                                                        private val crashesRepository: CrashesRepository): BaseSameFlowProxyViewModel<AppCrash>(
    application,
    database.appCrashDao().get(crashId)
) {
    companion object {
        const val EVENT_TYPE_COPY_LINK = "copy_link"
    }

    val uploadingStateData = MutableLiveData<Boolean>()

    fun uploadCrash(content: String) {
        launchCatching(Dispatchers.Main, { uploadingStateData.value = false }) {
            uploadingStateData.value = true

            val resultLink = foxBinRepository.uploadViaApi(content)
            sendEvent(EVENT_TYPE_COPY_LINK, resultLink)

            uploadingStateData.value = false
        }
    }

    fun deleteCrash(appCrash: AppCrash) {
        crashesRepository.deleteCrash(appCrash)
    }
}

@AssistedFactory
interface CrashDetailsViewModelAssistedFactory {
    fun create(crashId: Long): CrashDetailsViewModel
}