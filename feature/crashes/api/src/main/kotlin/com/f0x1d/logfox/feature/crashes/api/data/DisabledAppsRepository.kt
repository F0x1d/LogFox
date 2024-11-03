package com.f0x1d.logfox.feature.crashes.api.data

import com.f0x1d.logfox.arch.repository.DatabaseProxyRepository
import com.f0x1d.logfox.database.entity.DisabledApp
import kotlinx.coroutines.flow.Flow

interface DisabledAppsRepository : DatabaseProxyRepository<DisabledApp> {
    suspend fun isDisabledFor(packageName: String): Boolean
    fun disabledForFlow(packageName: String): Flow<Boolean>

    suspend fun checkApp(packageName: String)
    suspend fun checkApp(packageName: String, checked: Boolean)
}
