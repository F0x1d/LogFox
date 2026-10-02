package com.f0x1d.logfox.feature.filters.api.model

import com.f0x1d.logfox.core.recycler.Identifiable
import com.f0x1d.logfox.core.utils.GsonSkip
import com.f0x1d.logfox.feature.logging.api.model.LogLevel

data class UserFilter(
    @GsonSkip override val id: Long = 0,
    val including: Boolean = true,
    val allowedLevels: List<LogLevel> = emptyList(),
    val uid: String? = null,
    val pid: String? = null,
    val tid: String? = null,
    val packageName: String? = null,
    val tag: String? = null,
    val content: String? = null,
    @GsonSkip val enabled: Boolean = true,
) : Identifiable
