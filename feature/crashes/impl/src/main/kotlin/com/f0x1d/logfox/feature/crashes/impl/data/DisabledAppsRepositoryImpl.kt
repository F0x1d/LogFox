package com.f0x1d.logfox.feature.crashes.impl.data

import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.crashes.api.data.DisabledAppsRepository
import com.f0x1d.logfox.feature.crashes.api.model.DisabledApp
import com.f0x1d.logfox.feature.crashes.impl.mapper.toDomainModel
import com.f0x1d.logfox.feature.crashes.impl.mapper.toEntity
import com.f0x1d.logfox.feature.database.api.data.DisabledAppDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DisabledAppsRepositoryImpl @Inject constructor(
    private val disabledAppDataSource: DisabledAppDataSource,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : DisabledAppsRepository {

    override suspend fun isDisabledFor(packageName: String): Boolean = withContext(ioDispatcher) {
        disabledAppDataSource.getByPackageName(packageName) != null
    }

    override fun disabledForFlow(packageName: String): Flow<Boolean> = disabledAppDataSource
        .getByPackageNameAsFlow(packageName)
        .map { it != null }
        .flowOn(ioDispatcher)

    override suspend fun checkApp(packageName: String) = checkApp(
        packageName = packageName,
        checked = withContext(ioDispatcher) {
            disabledAppDataSource.getByPackageName(packageName) == null
        },
    )

    override suspend fun checkApp(packageName: String, checked: Boolean) = withContext(ioDispatcher) {
        if (checked) {
            disabledAppDataSource.insert(DisabledApp(packageName = packageName).toEntity())
        } else {
            disabledAppDataSource.deleteByPackageName(packageName)
        }
    }

    override fun getAllAsFlow(): Flow<List<DisabledApp>> = disabledAppDataSource.getAllAsFlow()
        .map { list -> list.map { it.toDomainModel() } }
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<DisabledApp?> = disabledAppDataSource.getByIdAsFlow(id)
        .map { it?.toDomainModel() }
        .flowOn(ioDispatcher)

    override suspend fun getAll(): List<DisabledApp> = withContext(ioDispatcher) {
        disabledAppDataSource.getAll().map { it.toDomainModel() }
    }

    override suspend fun getById(id: Long): DisabledApp? = withContext(ioDispatcher) {
        disabledAppDataSource.getById(id)?.toDomainModel()
    }

    override suspend fun update(item: DisabledApp) = withContext(ioDispatcher) {
        disabledAppDataSource.update(item.toEntity())
    }

    override suspend fun delete(item: DisabledApp) = withContext(ioDispatcher) {
        disabledAppDataSource.delete(item.toEntity())
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        disabledAppDataSource.deleteAll()
    }
}
