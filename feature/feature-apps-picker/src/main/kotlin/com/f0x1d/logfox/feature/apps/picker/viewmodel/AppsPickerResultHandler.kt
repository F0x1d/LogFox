package com.f0x1d.logfox.feature.apps.picker.viewmodel

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.model.InstalledApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface AppsPickerResultHandler {
    val supportsMultiplySelection: Boolean get() = false
    val checkedAppPackageNames: Flow<Set<String>> get() = flowOf(emptySet())

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
