package com.f0x1d.logfox.feature.crashes.impl.data

import android.content.Context
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
}
