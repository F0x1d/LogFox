package com.f0x1d.logfox.feature.crashes.presentation.details

import android.net.Uri
import com.f0x1d.logfox.feature.database.model.AppCrash

sealed interface CrashDetailsSideEffect {
    // Business logic side effects
    data object LoadCrash : CrashDetailsSideEffect

    data object ObservePreferences : CrashDetailsSideEffect

    data class ExportCrashToZip(val uri: Uri, val appCrash: AppCrash, val crashLog: String?) : CrashDetailsSideEffect

    data class ChangeBlacklist(val appCrash: AppCrash) : CrashDetailsSideEffect

    data class DeleteCrash(val appCrash: AppCrash) : CrashDetailsSideEffect

    // UI side effects
    data class CopyText(val text: String) : CrashDetailsSideEffect
}
