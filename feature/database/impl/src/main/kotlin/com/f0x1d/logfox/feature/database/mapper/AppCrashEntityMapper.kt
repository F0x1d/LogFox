package com.f0x1d.logfox.feature.database.mapper

import com.f0x1d.logfox.feature.database.entity.AppCrashEntity
import com.f0x1d.logfox.feature.database.entity.AppCrashRoomEntity

internal fun AppCrashRoomEntity.toData() = AppCrashEntity(
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

internal fun AppCrashEntity.toRoom() = AppCrashRoomEntity(
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
