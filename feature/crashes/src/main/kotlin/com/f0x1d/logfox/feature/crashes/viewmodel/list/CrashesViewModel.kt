package com.f0x1d.logfox.feature.crashes.viewmodel.list

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.AppCrashesCount
import com.f0x1d.logfox.database.entity.DisabledApp
import com.f0x1d.logfox.feature.apps.picker.viewmodel.AppsPickerResultHandler
import com.f0x1d.logfox.feature.crashes.core.repository.CrashesRepository
import com.f0x1d.logfox.feature.crashes.core.repository.DisabledAppsRepository
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.preferences.shared.crashes.CrashesSort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CrashesViewModel @Inject constructor(
    private val crashesRepository: CrashesRepository,
    private val disabledAppsRepository: DisabledAppsRepository,
    private val appPreferences: AppPreferences,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel(application), AppsPickerResultHandler {

    val currentSort get() = appPreferences.crashesSortType.get()
    val currentSortInReversedOrder get() = appPreferences.crashesSortReversedOrder.get()

    val crashes = combine(
        crashesRepository.getAllAsFlow(),
        appPreferences.crashesSortType.asFlow(),
        appPreferences.crashesSortReversedOrder.asFlow(),
    ) { crashes, sortType, sortInReversedOrder ->
        CrashesWithSort(
            crashes = crashes,
            sortType = sortType,
            sortInReversedOrder = sortInReversedOrder,
        )
    }
        .distinctUntilChanged()
        .map { crashesWithSort ->
            val groupedCrashes = crashesWithSort.crashes.groupBy { it.packageName }

            groupedCrashes.map {
                AppCrashesCount(
                    lastCrash = it.value.first(),
                    count = it.value.size
                )
            }.let(crashesWithSort.sortType.sorter).let { result ->
                if (crashesWithSort.sortInReversedOrder) {
                    result.asReversed()
                } else {
                    result
                }
            }
        }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    val query = MutableStateFlow("")

    val searchedCrashes = channelFlow {
        combine(crashesRepository.getAllAsFlow(), query) { crashes, query ->
            crashes to query
        }.collectLatest { (crashes, query) ->

            fun AppCrash.suits(query: String): Boolean =
                packageName.contains(query, ignoreCase = true)
                        || appName?.contains(query, ignoreCase = true) == true

            val filteredCrashes = withContext(defaultDispatcher) {
                crashes.filter { it.suits(query) }.map { AppCrashesCount(it) }
            }

            send(filteredCrashes)
            delay(SEARCH_DEBOUNCE_MILLIS) // Maybe no need to search content for now

            val deeplyFilteredCrashes = withContext(defaultDispatcher) {
                crashes.filter { crash ->
                    val fileContentSettles = withContext(ioDispatcher) {
                        crash.logFile?.readText()?.contains(query, ignoreCase = true) == true
                    }

                    crash.suits(query) || fileContentSettles
                }.map { AppCrashesCount(it) }
            }
            send(deeplyFilteredCrashes)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    fun updateQuery(query: String) = this.query.update { query }

    fun updateSort(sortType: CrashesSort, sortInReversedOrder: Boolean) = appPreferences.updateCrashesSortSettings(
        sortType = sortType,
        sortInReversedOrder = sortInReversedOrder,
    )

    fun deleteCrashesByPackageName(appCrash: AppCrash) = launchCatching {
        crashesRepository.deleteAllByPackageName(appCrash)
    }

    fun deleteCrash(appCrash: AppCrash) = launchCatching {
        crashesRepository.delete(appCrash)
    }

    fun clearCrashes() = launchCatching {
        crashesRepository.clear()
    }

    override val supportsMultiplySelection: Boolean = true

    override val checkedAppPackageNames: Flow<Set<String>> =
        disabledAppsRepository.getAllAsFlow().map { apps ->
            apps.map(DisabledApp::packageName).toSet()
        }

    override fun onAppChecked(app: InstalledApp, checked: Boolean) {
        viewModelScope.launch {
            disabledAppsRepository.checkApp(app.packageName, checked)
        }
    }

    override fun onAppSelected(app: InstalledApp): Boolean {
        viewModelScope.launch {
            disabledAppsRepository.checkApp(app.packageName)
        }
        return false
    }

    private data class CrashesWithSort(
        val crashes: List<AppCrash>,
        val sortType: CrashesSort,
        val sortInReversedOrder: Boolean,
    )

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 500L
    }
}
