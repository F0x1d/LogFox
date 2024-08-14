package com.f0x1d.logfox.feature.apps.picker.viewmodel

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.model.InstalledApp

interface AppsPickerResultHandler {
    // pass true to close fragment
    fun onAppSelected(app: InstalledApp): Boolean
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
