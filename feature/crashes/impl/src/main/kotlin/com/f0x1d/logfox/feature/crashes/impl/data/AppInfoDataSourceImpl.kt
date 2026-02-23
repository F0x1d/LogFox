package com.f0x1d.logfox.feature.crashes.impl.data

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class AppInfoDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppInfoDataSource {

    override fun getAppName(packageName: String): String? = runCatching {
        context.packageManager
            .getPackageInfo(packageName, 0)
            .applicationInfo
            ?.let(context.packageManager::getApplicationLabel)
            ?.toString()
    }.getOrNull()

    override fun getAppInfo(packageName: String): AppInfo {
        val packageInfo = runCatching {
            context.packageManager.getPackageInfo(packageName, 0)
        }.getOrNull()

        val appName = packageInfo?.applicationInfo
            ?.let(context.packageManager::getApplicationLabel)
            ?.toString()

        val versionName = packageInfo?.versionName

        val versionCode = packageInfo?.let {
            PackageInfoCompat.getLongVersionCode(it)
        }

        return AppInfo(
            appName = appName,
            packageName = packageName,
            versionName = versionName,
            versionCode = versionCode,
        )
    }
}
