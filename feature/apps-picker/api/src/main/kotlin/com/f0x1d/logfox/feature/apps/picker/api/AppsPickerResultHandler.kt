package com.f0x1d.logfox.feature.apps.picker.api

import android.content.Context
import com.f0x1d.logfox.feature.strings.Strings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface AppsPickerResultHandler {
    val supportsMultiplySelection: Boolean get() = false
    val checkedAppPackageNames: Flow<Set<String>> get() = flowOf(emptySet())

    fun providePickerTopAppBarTitle(context: Context) = context.getString(Strings.apps)

    fun onAppChecked(app: InstalledApp, checked: Boolean) = Unit

    // pass true to close fragment
    fun onAppSelected(app: InstalledApp): Boolean = false
}
