package com.f0x1d.logfox.feature.apps.picker.impl.data

import android.content.Context
import com.f0x1d.logfox.feature.apps.picker.api.InstalledApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class InstalledAppsDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : InstalledAppsDataSource {

    override fun getInstalledApps(): List<InstalledApp> = context.packageManager
        .getInstalledPackages(0)
        .map { packageInfo ->
            InstalledApp(
                title = packageInfo.applicationInfo?.loadLabel(context.packageManager).toString(),
                packageName = packageInfo.packageName,
            )
        }
        .sortedBy(InstalledApp::title)
}
