package com.f0x1d.logfox.viewmodel.crashes.list

import android.app.Application
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.di.viewmodel.AppName
import com.f0x1d.logfox.di.viewmodel.PackageName
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
class AppCrashesViewModel @Inject constructor(
    @PackageName val packageName: String,
    @AppName val appName: String?,
    private val database: AppDatabase,
    private val crashesRepository: CrashesRepository,
    application: Application
): BaseViewModel(application) {

    val crashes = database.appCrashDao().getAllAsFlow()
        .distinctUntilChanged()
        .map { crashes ->
            crashes.filter { crash ->
                crash.packageName == packageName
            }.map {
                AppCrashesCount(it)
            }
        }
        .flowOn(Dispatchers.IO)
        .asLiveData()

    fun deleteCrash(appCrash: AppCrash) = crashesRepository.delete(appCrash)
}
