package com.f0x1d.logfox.viewmodel

import android.app.Application
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class CrashDetailsViewModel @AssistedInject constructor(application: Application, database: AppDatabase, @Assisted crashId: Long): BaseSameFlowProxyViewModel<AppCrash>(
    application,
    database.appCrashDao().get(crashId)
)

@AssistedFactory
interface CrashDetailsViewModelAssistedFactory {
    fun create(crashId: Long): CrashDetailsViewModel
}