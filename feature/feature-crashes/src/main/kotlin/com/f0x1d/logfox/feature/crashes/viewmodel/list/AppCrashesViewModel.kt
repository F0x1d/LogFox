package com.f0x1d.logfox.feature.crashes.viewmodel.list

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.feature.crashes.core.repository.CrashesRepository
import com.f0x1d.logfox.feature.crashes.di.AppName
import com.f0x1d.logfox.feature.crashes.di.PackageName
import com.f0x1d.logfox.feature.crashes.model.AppCrashesCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppCrashesViewModel @Inject constructor(
    @PackageName val packageName: String,
    @AppName val appName: String?,
    private val crashesRepository: CrashesRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel(application) {

    val crashes = crashesRepository.getAllAsFlow()
        .map { crashes ->
            crashes.filter { crash ->
                crash.packageName == packageName
            }.map {
                AppCrashesCount(it)
            }
        }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    fun deleteCrash(appCrash: AppCrash) = launchCatching {
        crashesRepository.delete(appCrash)
    }
}
