package com.f0x1d.logfox.feature.filters.presentation.edit

import android.net.Uri
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.logging.api.model.LogLevel

sealed interface EditFilterSideEffect {
    // Business logic side effects - handled by EffectHandler
    data class LoadFilter(val filterId: Long?) : EditFilterSideEffect
    data class SaveFilter(
        val filter: UserFilter?,
        val including: Boolean,
        val enabled: Boolean,
        val enabledLogLevels: List<LogLevel>,
        val uid: String?,
        val pid: String?,
        val tid: String?,
        val packageName: String?,
        val tag: String?,
        val content: String?,
    ) : EditFilterSideEffect
    data class ExportFilter(val uri: Uri, val filter: UserFilter?) : EditFilterSideEffect

    // UI side effects - handled by Fragment
    data class UpdatePackageNameField(val packageName: String) : EditFilterSideEffect
    data object NavigateToAppPicker : EditFilterSideEffect
    data object Close : EditFilterSideEffect
}
