package com.f0x1d.logfox.feature.database.impl.data

import com.f0x1d.logfox.feature.database.api.data.DisabledAppDataSource
import com.f0x1d.logfox.feature.database.api.entity.DisabledAppEntity
import com.f0x1d.logfox.feature.database.impl.data.dao.DisabledAppDao
import com.f0x1d.logfox.feature.database.impl.mapper.toData
import com.f0x1d.logfox.feature.database.impl.mapper.toRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class DisabledAppDataSourceImpl @Inject constructor(
    private val dao: DisabledAppDao,
) : DisabledAppDataSource {

    override suspend fun getAll(): List<DisabledAppEntity> = dao.getAll().map { it.toData() }

    override fun getAllAsFlow(): Flow<List<DisabledAppEntity>> =
        dao.getAllAsFlow().map { list -> list.map { it.toData() } }

    override suspend fun getById(id: Long): DisabledAppEntity? = dao.getById(id)?.toData()

    override fun getByIdAsFlow(id: Long): Flow<DisabledAppEntity?> =
        dao.getByIdAsFlow(id).map { it?.toData() }

    override suspend fun getByPackageName(packageName: String): DisabledAppEntity? =
        dao.getByPackageName(packageName)?.toData()

    override fun getByPackageNameAsFlow(packageName: String): Flow<DisabledAppEntity?> =
        dao.getByPackageNameAsFlow(packageName).map { it?.toData() }

    override suspend fun insert(item: DisabledAppEntity) = dao.insert(item.toRoom())

    override suspend fun update(item: DisabledAppEntity) = dao.update(item.toRoom())

    override suspend fun delete(item: DisabledAppEntity) = dao.delete(item.toRoom())

    override suspend fun deleteByPackageName(packageName: String) = dao.deleteByPackageName(packageName)

    override suspend fun deleteAll() = dao.deleteAll()
}
