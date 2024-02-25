package com.f0x1d.logfox.viewmodel.crashes.list

import android.app.Application
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.model.AppCrashesCount
import com.f0x1d.logfox.repository.logging.CrashesRepository
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CrashesViewModel @Inject constructor(
    private val database: AppDatabase,
    private val crashesRepository: CrashesRepository,
    application: Application
): BaseViewModel(application) {

    val crashes = database.appCrashDao().getAllAsFlow()
        .distinctUntilChanged()
        .map { crashes ->
            val groupedCrashes = crashes.groupBy { it.packageName }

            groupedCrashes.map {
                AppCrashesCount(
                    lastCrash = it.value.first(),
                    count = it.value.size
                )
            }
        }
        .flowOn(Dispatchers.IO)
        .asLiveData()

    fun deleteCrashesByPackageName(appCrash: AppCrash) = crashesRepository.deleteAllByPackageName(appCrash)

    fun clearCrashes() = crashesRepository.clear()
}