package com.f0x1d.logfox.feature.filters.presentation.edit

import com.f0x1d.logfox.feature.filters.api.model.MatchMode
import com.f0x1d.logfox.feature.filters.api.model.UserFilter

internal data class EditFilterState(
    val filter: UserFilter?,
    val name: String?,
    val including: Boolean,
    val enabled: Boolean,
    val enabledLogLevels: List<Boolean>,
    val uid: String?,
    val pid: String?,
    val tid: String?,
    val packageName: String?,
    val tag: String?,
    val tagMatchMode: MatchMode,
    val content: String?,
    val contentMatchMode: MatchMode,
    // Flipped to true by the reducer on any edit; used to confirm before discarding unsaved changes.
    val isDirty: Boolean = false,
)
