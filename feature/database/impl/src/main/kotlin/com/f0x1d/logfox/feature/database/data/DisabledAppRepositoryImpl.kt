package com.f0x1d.logfox.feature.database.data

import com.f0x1d.logfox.feature.database.dao.DisabledAppDao
import com.f0x1d.logfox.feature.database.mapper.toDomain
import com.f0x1d.logfox.feature.database.mapper.toEntity
import com.f0x1d.logfox.feature.database.model.DisabledApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class DisabledAppRepositoryImpl @Inject constructor(
    private val dao: DisabledAppDao,
) : DisabledAppRepository {

    override suspend fun getAll(): List<DisabledApp> =
        dao.getAll().map { it.toDomain() }

    override fun getAllAsFlow(): Flow<List<DisabledApp>> =
        dao.getAllAsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): DisabledApp? =
        dao.getById(id)?.toDomain()

    override fun getByIdAsFlow(id: Long): Flow<DisabledApp?> =
        dao.getByIdAsFlow(id).map { it?.toDomain() }

    override suspend fun getByPackageName(packageName: String): DisabledApp? =
        dao.getByPackageName(packageName)?.toDomain()

    override fun getByPackageNameAsFlow(packageName: String): Flow<DisabledApp?> =
        dao.getByPackageNameAsFlow(packageName).map { it?.toDomain() }

    override suspend fun insert(item: DisabledApp) =
        dao.insert(item.toEntity())

    override suspend fun update(item: DisabledApp) =
        dao.update(item.toEntity())

    override suspend fun delete(item: DisabledApp) =
        dao.delete(item.toEntity())

    override suspend fun deleteByPackageName(packageName: String) =
        dao.deleteByPackageName(packageName)

    override suspend fun deleteAll() =
        dao.deleteAll()
}
