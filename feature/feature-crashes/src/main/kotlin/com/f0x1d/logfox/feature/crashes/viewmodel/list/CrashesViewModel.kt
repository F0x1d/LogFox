package com.f0x1d.logfox.feature.crashes.viewmodel.list

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.feature.crashes.core.repository.CrashesRepository
import com.f0x1d.logfox.feature.crashes.model.AppCrashesCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CrashesViewModel @Inject constructor(
    private val crashesRepository: CrashesRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel(application) {

    val crashes = crashesRepository.getAllAsFlow()
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
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    fun deleteCrashesByPackageName(appCrash: AppCrash) = launchCatching(ioDispatcher) {
        crashesRepository.deleteAllByPackageName(appCrash)
    }

    fun clearCrashes() = launchCatching(ioDispatcher) {
        crashesRepository.clear()
    }
}
