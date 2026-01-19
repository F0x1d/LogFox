package com.f0x1d.logfox.feature.database.mapper

import com.f0x1d.logfox.feature.database.entity.AppCrashEntity
import com.f0x1d.logfox.feature.database.model.AppCrash

internal fun AppCrashEntity.toDomain() = AppCrash(
    id = id,
    appName = appName,
    packageName = packageName,
    crashType = crashType,
    dateAndTime = dateAndTime,
    logFile = logFile,
    logDumpFile = logDumpFile,
)

internal fun AppCrash.toEntity(
    isDeleted: Boolean = false,
    deletedTime: Long? = null,
) = AppCrashEntity(
    id = id,
    appName = appName,
    packageName = packageName,
    crashType = crashType,
    dateAndTime = dateAndTime,
    logFile = logFile,
    logDumpFile = logDumpFile,
    isDeleted = isDeleted,
    deletedTime = deletedTime,
)
