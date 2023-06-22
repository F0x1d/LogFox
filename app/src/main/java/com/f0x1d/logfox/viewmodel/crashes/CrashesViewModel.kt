package com.f0x1d.logfox.viewmodel.crashes

import android.app.Application
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.repository.logging.CrashesRepository
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CrashesViewModel @Inject constructor(application: Application, private val crashesRepository: CrashesRepository): BaseSameFlowProxyViewModel<List<AppCrash>>(
    application,
    crashesRepository.itemsFlow
) {
    fun deleteCrash(appCrash: AppCrash) = crashesRepository.delete(appCrash)

    fun clearCrashes() = crashesRepository.clear()
}