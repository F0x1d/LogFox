package com.f0x1d.logfox.feature.filters.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.model.InstalledApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChooseAppViewModel @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel(application) {

    val apps = MutableStateFlow(emptyList<InstalledApp>())
    val query = MutableStateFlow("")

    val searchedApps = combine(apps, query) { apps, query ->
        apps to query
    }.map {
        it.first.filter { app ->
            app.title.toString().contains(it.second) || app.packageName.contains(it.second)
        }
    }.flowOn(
        defaultDispatcher,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    init {
        load()
    }

    fun updateQuery(text: String) = query.update { text }

    private fun load() = launchCatching(defaultDispatcher) {
        val packageManager = ctx.packageManager

        val installedApps = packageManager.getInstalledPackages(0).map {
            InstalledApp(
                title = it.applicationInfo.loadLabel(packageManager),
                packageName = it.packageName,
            )
        }.sortedBy {
            it.title.toString()
        }

        apps.update {
            installedApps
        }
    }
}
