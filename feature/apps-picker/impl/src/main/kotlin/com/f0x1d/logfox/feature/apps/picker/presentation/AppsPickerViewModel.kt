package com.f0x1d.logfox.feature.apps.picker.presentation

import android.app.Application
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AppsPickerViewModel @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel<AppsPickerState, AppsPickerAction>(
    initialStateProvider = { AppsPickerState() },
    application = application,
) {
    init {
        load()
    }

    fun performBackAction(popBackStack: () -> Unit) = reduce {
        if (searchActive) {
            copy(searchActive = false)
        } else {
            popBackStack()
            this
        }
    }

    fun changeSearchActive(active: Boolean) = reduce {
        copy(searchActive = active)
    }

    fun updateQuery(query: String) = reduce {
        copy(query = query)
    }

    private fun load() = launchCatching(defaultDispatcher) {
        val packageManager = ctx.packageManager

        val installedApps = packageManager.getInstalledPackages(0).map {
            InstalledApp(
                title = it.applicationInfo?.loadLabel(packageManager).toString(),
                packageName = it.packageName,
            )
        }.sortedBy(InstalledApp::title)

        reduce {
            copy(
                apps = installedApps.toImmutableList(),
                isLoading = false,
            )
        }

        state.map { state ->
            state.query
        }.distinctUntilChanged().map { query ->
            installedApps.filter { app ->
                app.title.contains(query, ignoreCase = true)
                        || app.packageName.contains(query, ignoreCase = true)
            }
        }.flowOn(
            defaultDispatcher,
        ).collectLatest { apps ->
            reduce { copy(searchedApps = apps.toImmutableList()) }
        }
    }
}
