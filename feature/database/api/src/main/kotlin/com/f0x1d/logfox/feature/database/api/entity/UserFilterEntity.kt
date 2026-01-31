package com.f0x1d.logfox.feature.database.api.entity

import com.f0x1d.logfox.feature.logging.api.model.LogLevel

data class UserFilterEntity(
    val id: Long = 0,
    val including: Boolean = true,
    val allowedLevels: List<LogLevel> = emptyList(),
    val uid: String? = null,
    val pid: String? = null,
    val tid: String? = null,
    val packageName: String? = null,
    val tag: String? = null,
    val content: String? = null,
    val enabled: Boolean = true,
)
