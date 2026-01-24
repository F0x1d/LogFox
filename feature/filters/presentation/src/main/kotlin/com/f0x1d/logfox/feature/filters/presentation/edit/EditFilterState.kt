package com.f0x1d.logfox.feature.filters.presentation.edit

import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.logging.api.model.LogLevel

data class EditFilterState(
    val filter: UserFilter? = null,
    val including: Boolean = true,
    val enabled: Boolean = true,
    val enabledLogLevels: List<Boolean> = List(LogLevel.entries.size) { false },
    val uid: String? = null,
    val pid: String? = null,
    val tid: String? = null,
    val packageName: String? = null,
    val tag: String? = null,
    val content: String? = null,
)
