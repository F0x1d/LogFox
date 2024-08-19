package com.f0x1d.logfox.feature.apps.picker.viewmodel

import android.app.Application
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseStateViewModel
import com.f0x1d.logfox.feature.apps.picker.ui.fragment.picker.compose.AppsPickerScreenState
import com.f0x1d.logfox.model.InstalledApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppsPickerViewModel @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    application: Application,
): BaseStateViewModel<AppsPickerScreenState>(
    initialStateProvider = { AppsPickerScreenState() },
    application = application,
) {

    private val query = MutableStateFlow("")

    init {
        load()
    }

    fun performBackAction(popBackStack: () -> Unit) = state {
        if (searchActive) {
            copy(searchActive = false)
        } else {
            popBackStack()
            this
        }
    }

    fun changeSearchActive(active: Boolean) = state {
        copy(searchActive = active)
    }

    fun updateQuery(text: String) = state {
        copy(query = text)
    }.also {
        query.update { text }
    }

    private fun load() = launchCatching(defaultDispatcher) {
        val packageManager = ctx.packageManager

        val installedApps = packageManager.getInstalledPackages(0).map {
            InstalledApp(
                title = it.applicationInfo.loadLabel(packageManager).toString(),
                packageName = it.packageName,
            )
        }.sortedBy(InstalledApp::title)

        state {
            copy(
                apps = installedApps.toImmutableList(),
                isLoading = false,
            )
        }

        query.map { query ->
            installedApps.filter { app ->
                app.title.contains(query, ignoreCase = true)
                        || app.packageName.contains(query, ignoreCase = true)
            }
        }.flowOn(
            defaultDispatcher,
        ).collectLatest { apps ->
            state { copy(searchedApps = apps.toImmutableList()) }
        }
    }
}
