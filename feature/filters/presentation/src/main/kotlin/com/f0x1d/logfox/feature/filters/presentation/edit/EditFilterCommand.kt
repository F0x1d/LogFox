package com.f0x1d.logfox.feature.filters.presentation.edit

import android.net.Uri
import com.f0x1d.logfox.feature.database.model.UserFilter

sealed interface EditFilterCommand {
    data object Load : EditFilterCommand
    data class FilterLoaded(val filter: UserFilter) : EditFilterCommand

    // Form field updates
    data class UpdateUid(val uid: String) : EditFilterCommand
    data class UpdatePid(val pid: String) : EditFilterCommand
    data class UpdateTid(val tid: String) : EditFilterCommand
    data class UpdatePackageName(val packageName: String) : EditFilterCommand
    data class UpdateTag(val tag: String) : EditFilterCommand
    data class UpdateContent(val content: String) : EditFilterCommand

    // Actions
    data object ToggleIncluding : EditFilterCommand
    data object ToggleEnabled : EditFilterCommand
    data class FilterLevel(val which: Int, val filtering: Boolean) : EditFilterCommand
    data object Save : EditFilterCommand
    data class Export(val uri: Uri) : EditFilterCommand

    // App picker result
    data class AppSelected(val packageName: String) : EditFilterCommand

    // Navigation
    data object SelectApp : EditFilterCommand
}
