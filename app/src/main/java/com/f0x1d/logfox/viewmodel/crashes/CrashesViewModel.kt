package com.f0x1d.logfox.viewmodel.crashes

import android.app.Application
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.repository.logging.CrashesRepository
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class CrashesViewModel @Inject constructor(
    application: Application,
    private val database: AppDatabase,
    private val crashesRepository: CrashesRepository
): BaseViewModel(application) {

    val crashes = database.appCrashDao().getAllAsFlow()
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
        .asLiveData()

    fun deleteCrash(appCrash: AppCrash) = crashesRepository.delete(appCrash)

    fun clearCrashes() = crashesRepository.clear()
}