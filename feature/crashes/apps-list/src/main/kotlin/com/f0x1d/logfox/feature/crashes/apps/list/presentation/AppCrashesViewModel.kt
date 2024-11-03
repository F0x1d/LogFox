package com.f0x1d.logfox.feature.crashes.apps.list.presentation

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.AppCrashesCount
import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.apps.list.di.AppName
import com.f0x1d.logfox.feature.crashes.apps.list.di.PackageName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppCrashesViewModel @Inject constructor(
    @PackageName val packageName: String,
    @AppName val appName: String?,
    private val crashesRepository: CrashesRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel<AppCrashesState, AppCrashesAction>(
    initialStateProvider = { AppCrashesState() },
    application = application,
) {
    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            crashesRepository.getAllAsFlow()
                .map { crashes ->
                    crashes.filter { crash ->
                        crash.packageName == packageName
                    }.map {
                        AppCrashesCount(it)
                    }
                }
                .flowOn(defaultDispatcher)
                .collect { crashes ->
                    reduce {
                        copy(crashes = crashes)
                    }
                }
        }
    }

    fun deleteCrash(appCrash: AppCrash) = launchCatching {
        crashesRepository.delete(appCrash)
    }
}
