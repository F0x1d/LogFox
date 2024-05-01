package com.f0x1d.logfox.viewmodel.filters

import android.app.Application
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChooseAppViewModel @Inject constructor(
    application: Application
): BaseViewModel(application) {

    val apps = MutableStateFlow(emptyList<com.f0x1d.logfox.model.InstalledApp>())
    val query = MutableStateFlow("")

    val searchedApps = combine(apps, query) { apps, query ->
        apps to query
    }.map {
        it.first.filter { app ->
            app.title.toString().contains(it.second) || app.packageName.contains(it.second)
        }
    }.flowOn(
        Dispatchers.IO
    ).distinctUntilChanged().asLiveData()

    init {
        load()
    }

    private fun load() = launchCatching(Dispatchers.IO) {
        val packageManager = ctx.packageManager

        val installedApps = packageManager.getInstalledPackages(0).map {
            com.f0x1d.logfox.model.InstalledApp(
                title = it.applicationInfo.loadLabel(packageManager),
                packageName = it.packageName
            )
        }.sortedBy {
            it.title.toString()
        }

        apps.update {
            installedApps
        }
    }
}
