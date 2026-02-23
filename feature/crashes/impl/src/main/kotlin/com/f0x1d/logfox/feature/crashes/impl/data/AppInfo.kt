package com.f0x1d.logfox.feature.crashes.impl.data

internal data class AppInfo(
    val appName: String?,
    val packageName: String,
    val versionName: String?,
    val versionCode: Long?,
) {

    fun format(): String = buildList<String> {
        appName?.let { add("APP_NAME: $it") }
        add("PACKAGE: $packageName")
        versionName?.let { add("VERSION_NAME: $it") }
        versionCode?.let { add("VERSION_CODE: $it") }
    }.joinToString("\n")
}
