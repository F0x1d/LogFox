package com.f0x1d.logfox.feature.database.impl.data

import com.f0x1d.logfox.feature.database.api.data.UserFilterDataSource
import com.f0x1d.logfox.feature.database.api.entity.UserFilterEntity
import com.f0x1d.logfox.feature.database.impl.data.dao.UserFilterDao
import com.f0x1d.logfox.feature.database.impl.mapper.toData
import com.f0x1d.logfox.feature.database.impl.mapper.toRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserFilterDataSourceImpl @Inject constructor(
    private val dao: UserFilterDao,
) : UserFilterDataSource {

    override fun getAllAsFlow(): Flow<List<UserFilterEntity>> =
        dao.getAllAsFlow().map { list -> list.map { it.toData() } }

    override fun getAllEnabledAsFlow(): Flow<List<UserFilterEntity>> =
        dao.getAllEnabledAsFlow().map { list -> list.map { it.toData() } }

    override suspend fun getAll(): List<UserFilterEntity> = dao.getAll().map { it.toData() }

    override fun getByIdAsFlow(id: Long): Flow<UserFilterEntity?> =
        dao.getByIdAsFlow(id).map { it?.toData() }

    override suspend fun getById(id: Long): UserFilterEntity? = dao.getById(id)?.toData()

    override suspend fun insert(userFilter: UserFilterEntity) = dao.insert(userFilter.toRoom())

    override suspend fun insert(items: List<UserFilterEntity>) = dao.insert(items.map { it.toRoom() })

    override suspend fun update(userFilter: UserFilterEntity) = dao.update(userFilter.toRoom())

    override suspend fun delete(userFilter: UserFilterEntity) = dao.delete(userFilter.toRoom())

    override suspend fun deleteAll() = dao.deleteAll()
}
