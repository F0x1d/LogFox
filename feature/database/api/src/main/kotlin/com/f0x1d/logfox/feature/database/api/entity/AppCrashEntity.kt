package com.f0x1d.logfox.feature.database.api.entity

import java.io.File

data class AppCrashEntity(
    val id: Long = 0,
    val appName: String?,
    val packageName: String,
    val crashType: CrashType,
    val dateAndTime: Long,
    val logFile: File? = null,
    val logDumpFile: File? = null,
    val isDeleted: Boolean = false,
    val deletedTime: Long? = null,
)
