package com.f0x1d.logfox.feature.crashes.list.presentation

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.AppCrashesCount
import com.f0x1d.logfox.database.entity.DisabledApp
import com.f0x1d.logfox.feature.apps.picker.AppsPickerResultHandler
import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.data.DisabledAppsRepository
import com.f0x1d.logfox.preferences.shared.AppPreferences
import com.f0x1d.logfox.preferences.shared.crashes.CrashesSort
import com.f0x1d.logfox.strings.Strings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrashesViewModel @Inject constructor(
    private val crashesRepository: CrashesRepository,
    private val disabledAppsRepository: DisabledAppsRepository,
    private val appPreferences: AppPreferences,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel<CrashesState, CrashesAction>(
    initialStateProvider = { CrashesState() },
    application = application,
), AppsPickerResultHandler {
    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            combine(
                crashesRepository.getAllAsFlow(),
                appPreferences.crashesSortType.asFlow(),
                appPreferences.crashesSortReversedOrder.asFlow(),
            ) { crashes, sortType, sortInReversedOrder ->
                val groupedCrashes = crashes.groupBy { it.packageName }

                val appCrashes = groupedCrashes.map {
                    AppCrashesCount(
                        lastCrash = it.value.first(),
                        count = it.value.size
                    )
                }.let(sortType.sorter).let { result ->
                    if (sortInReversedOrder) {
                        result.asReversed()
                    } else {
                        result
                    }
                }

                CrashesWithSort(
                    crashes = appCrashes,
                    sortType = sortType,
                    sortInReversedOrder = sortInReversedOrder,
                )
            }
                .distinctUntilChanged()
                .flowOn(defaultDispatcher)
                .onEach { data ->
                    reduce {
                        copy(
                            crashes = data.crashes,
                            currentSort = data.sortType,
                            sortInReversedOrder = data.sortInReversedOrder,
                        )
                    }
                }
                .launchIn(this)

            combine(
                crashesRepository.getAllAsFlow().distinctUntilChanged(),
                state.map { it.query.orEmpty() },
            ) { crashes, query -> crashes to query }
                .map { (crashes, query) ->
                    crashes.filter { crash ->
                        crash.packageName.contains(query, ignoreCase = true)
                                || crash.appName?.contains(query, ignoreCase = true) == true
                    }.map { AppCrashesCount(it) }
                }
                .distinctUntilChanged()
                .flowOn(defaultDispatcher)
                .onEach { searchedCrashes ->
                    reduce { copy(searchedCrashes = searchedCrashes) }
                }
                .launchIn(this)
        }
    }

    fun updateQuery(query: String) = reduce {
        copy(query = query)
    }

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

    override fun providePickerTopAppBarTitle(context: Context): String =
        context.getString(Strings.blacklist)

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
        val crashes: List<AppCrashesCount>,
        val sortType: CrashesSort,
        val sortInReversedOrder: Boolean,
    )
}
