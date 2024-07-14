package com.f0x1d.logfox.feature.crashes.viewmodel.list

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.feature.crashes.core.repository.CrashesRepository
import com.f0x1d.logfox.feature.crashes.model.AppCrashesCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CrashesViewModel @Inject constructor(
    private val crashesRepository: CrashesRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
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
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    val query = MutableStateFlow("")

    val searchedCrashes = combine(
        crashesRepository.getAllAsFlow(),
        query,
    ) { crashes, query ->
        crashes to query
    }.debounce(
        timeoutMillis = 100,
    ).map { (crashes, query) ->
        crashes.filter { crash ->
            crash.packageName.contains(query, ignoreCase = true)
                    || crash.appName?.contains(query, ignoreCase = true) == true
        }.map { AppCrashesCount(it) }
    }.flowOn(
        defaultDispatcher,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    fun updateQuery(query: String) = this.query.update { query }

    fun deleteCrashesByPackageName(appCrash: AppCrash) = launchCatching {
        crashesRepository.deleteAllByPackageName(appCrash)
    }

    fun deleteCrash(appCrash: AppCrash) = launchCatching {
        crashesRepository.delete(appCrash)
    }

    fun clearCrashes() = launchCatching {
        crashesRepository.clear()
    }
}
