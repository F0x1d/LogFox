package com.f0x1d.logfox.feature.crashes.api.repository

import com.f0x1d.logfox.arch.repository.DatabaseProxyRepository
import com.f0x1d.logfox.database.entity.AppCrash

interface CrashesRepository : DatabaseProxyRepository<AppCrash> {
    suspend fun getAllByDateAndTime(
        dateAndTime: Long,
        packageName: String,
    ): List<AppCrash>

    suspend fun insert(appCrash: AppCrash): Long

    suspend fun deleteAllByPackageName(appCrash: AppCrash)
}
