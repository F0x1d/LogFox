package com.f0x1d.logfox.feature.crashes.impl.data

import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.database.data.DisabledAppRepository
import com.f0x1d.logfox.feature.database.model.DisabledApp
import com.f0x1d.logfox.feature.crashes.api.data.DisabledAppsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DisabledAppsRepositoryImpl @Inject constructor(
    private val disabledAppRepository: DisabledAppRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : DisabledAppsRepository {

    override suspend fun isDisabledFor(packageName: String): Boolean = withContext(ioDispatcher) {
        disabledAppRepository.getByPackageName(packageName) != null
    }

    override fun disabledForFlow(packageName: String): Flow<Boolean> =
        disabledAppRepository
            .getByPackageNameAsFlow(packageName)
            .map { it != null }
            .flowOn(ioDispatcher)

    override suspend fun checkApp(packageName: String) = checkApp(
        packageName = packageName,
        checked = withContext(ioDispatcher) {
            disabledAppRepository.getByPackageName(packageName) == null
        },
    )

    override suspend fun checkApp(packageName: String, checked: Boolean) = withContext(ioDispatcher) {
        if (checked) {
            disabledAppRepository.insert(DisabledApp(packageName = packageName))
        } else {
            disabledAppRepository.deleteByPackageName(packageName)
        }
    }

    override fun getAllAsFlow(): Flow<List<DisabledApp>> =
        disabledAppRepository.getAllAsFlow()
            .distinctUntilChanged()
            .flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<DisabledApp?> =
        disabledAppRepository.getByIdAsFlow(id).flowOn(ioDispatcher)

    override suspend fun getAll(): List<DisabledApp> = withContext(ioDispatcher) {
        disabledAppRepository.getAll()
    }

    override suspend fun getById(id: Long): DisabledApp? = withContext(ioDispatcher) {
        disabledAppRepository.getById(id)
    }

    override suspend fun update(item: DisabledApp) = withContext(ioDispatcher) {
        disabledAppRepository.update(item)
    }

    override suspend fun delete(item: DisabledApp) = withContext(ioDispatcher) {
        disabledAppRepository.delete(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        disabledAppRepository.deleteAll()
    }
}
