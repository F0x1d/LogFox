package com.f0x1d.logfox.feature.crashes.impl.mapper

import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import com.f0x1d.logfox.feature.crashes.api.model.CrashType
import com.f0x1d.logfox.feature.database.entity.AppCrashEntity
import com.f0x1d.logfox.feature.database.entity.CrashType as EntityCrashType

internal fun AppCrashEntity.toDomainModel() = AppCrash(
    id = id,
    appName = appName,
    packageName = packageName,
    crashType = crashType.toDomainModel(),
    dateAndTime = dateAndTime,
    logFile = logFile,
    logDumpFile = logDumpFile,
)

internal fun AppCrash.toEntity(isDeleted: Boolean = false, deletedTime: Long? = null) = AppCrashEntity(
    id = id,
    appName = appName,
    packageName = packageName,
    crashType = crashType.toEntity(),
    dateAndTime = dateAndTime,
    logFile = logFile,
    logDumpFile = logDumpFile,
    isDeleted = isDeleted,
    deletedTime = deletedTime,
)

private fun EntityCrashType.toDomainModel(): CrashType = when (this) {
    EntityCrashType.JAVA -> CrashType.JAVA
    EntityCrashType.JNI -> CrashType.JNI
    EntityCrashType.ANR -> CrashType.ANR
}

private fun CrashType.toEntity(): EntityCrashType = when (this) {
    CrashType.JAVA -> EntityCrashType.JAVA
    CrashType.JNI -> EntityCrashType.JNI
    CrashType.ANR -> EntityCrashType.ANR
}
