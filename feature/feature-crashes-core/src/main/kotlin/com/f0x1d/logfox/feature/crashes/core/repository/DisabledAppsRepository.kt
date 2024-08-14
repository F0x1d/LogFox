package com.f0x1d.logfox.feature.crashes.core.repository

import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.repository.DatabaseProxyRepository
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.DisabledApp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface DisabledAppsRepository : DatabaseProxyRepository<DisabledApp> {
    suspend fun isDisabledFor(packageName: String): Boolean

    suspend fun checkApp(packageName: String)
    suspend fun checkApp(packageName: String, checked: Boolean)
}

internal class DisabledAppsRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : DisabledAppsRepository {

    override suspend fun isDisabledFor(packageName: String): Boolean = withContext(ioDispatcher) {
        database.disabledApps().getByPackageName(packageName) != null
    }

    override suspend fun checkApp(packageName: String) = checkApp(
        packageName = packageName,
        checked = withContext(ioDispatcher) {
            database.disabledApps().getByPackageName(packageName) == null
        },
    )

    override suspend fun checkApp(packageName: String, checked: Boolean) = withContext(ioDispatcher) {
        if (checked) {
            database.disabledApps().insert(DisabledApp(packageName))
        } else {
            database.disabledApps().deleteByPackageName(packageName)
        }
    }

    override fun getAllAsFlow(): Flow<List<DisabledApp>> =
        database.disabledApps().getAllAsFlow()
            .distinctUntilChanged()
            .flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<DisabledApp?> =
        database.disabledApps().getByIdAsFlow(id).flowOn(ioDispatcher)

    override suspend fun getAll(): List<DisabledApp> = withContext(ioDispatcher) {
        database.disabledApps().getAll()
    }

    override suspend fun getById(id: Long): DisabledApp? = withContext(ioDispatcher) {
        database.disabledApps().getById(id)
    }

    override suspend fun update(item: DisabledApp) = withContext(ioDispatcher) {
        database.disabledApps().update(item)
    }

    override suspend fun delete(item: DisabledApp) = withContext(ioDispatcher) {
        database.disabledApps().delete(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        database.disabledApps().deleteAll()
    }
}
