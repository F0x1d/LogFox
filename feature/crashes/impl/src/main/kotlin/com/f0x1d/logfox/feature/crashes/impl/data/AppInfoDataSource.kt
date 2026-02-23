package com.f0x1d.logfox.feature.crashes.impl.data

internal interface AppInfoDataSource {
    fun getAppName(packageName: String): String?
    fun getAppInfo(packageName: String): AppInfo
}
