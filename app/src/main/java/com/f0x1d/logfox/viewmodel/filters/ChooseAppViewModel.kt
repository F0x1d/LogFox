package com.f0x1d.logfox.viewmodel.filters

import android.app.Application
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChooseAppViewModel @Inject constructor(
    application: Application
): BaseViewModel(application) {
    val apps = MutableStateFlow(emptyList<InstalledApp>())

    init {
        load()
    }

    private fun load() = launchCatching(Dispatchers.IO) {
        val packageManager = ctx.packageManager

        val installedApps = packageManager.getInstalledPackages(0).map {
            InstalledApp(
                it.applicationInfo.loadLabel(packageManager),
                it.packageName
            )
        }.sortedBy { it.title.toString() }

        apps.update {
            installedApps
        }
    }
}