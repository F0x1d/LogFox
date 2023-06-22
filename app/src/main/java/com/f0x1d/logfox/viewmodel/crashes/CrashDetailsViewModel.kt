package com.f0x1d.logfox.viewmodel.crashes

import android.app.Application
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.repository.logging.CrashesRepository
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class CrashDetailsViewModel @AssistedInject constructor(
    @Assisted crashId: Long,
    application: Application,
    database: AppDatabase,
    private val crashesRepository: CrashesRepository
): BaseSameFlowProxyViewModel<AppCrash>(application, database.appCrashDao().get(crashId)) {

    companion object {
        const val EVENT_TYPE_COPY_LINK = "copy_link"
    }

    fun deleteCrash(appCrash: AppCrash) = crashesRepository.delete(appCrash)
}

@AssistedFactory
interface CrashDetailsViewModelAssistedFactory {
    fun create(crashId: Long): CrashDetailsViewModel
}