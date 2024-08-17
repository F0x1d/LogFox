package com.f0x1d.logfox.feature.apps.picker.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.strings.Strings
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

@SuppressLint("RestrictedApi")
internal fun Fragment.resultHandler(): Lazy<AppsPickerResultHandler?> = lazy {
    val backStackEntry = findNavController().previousBackStackEntry
        ?: return@lazy null

    val store = backStackEntry.viewModelStore
    val availableViewModelKeys = store.keys()

    availableViewModelKeys
        .firstNotNullOfOrNull { store[it] as? AppsPickerResultHandler }
}
