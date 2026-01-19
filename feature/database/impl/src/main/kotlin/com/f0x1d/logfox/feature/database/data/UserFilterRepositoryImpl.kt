package com.f0x1d.logfox.feature.database.data

import com.f0x1d.logfox.feature.database.dao.UserFilterDao
import com.f0x1d.logfox.feature.database.mapper.toDomain
import com.f0x1d.logfox.feature.database.mapper.toEntity
import com.f0x1d.logfox.feature.database.model.UserFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserFilterRepositoryImpl @Inject constructor(private val dao: UserFilterDao) : UserFilterRepository {

    override fun getAllAsFlow(): Flow<List<UserFilter>> = dao.getAllAsFlow().map { list -> list.map { it.toDomain() } }

    override fun getAllEnabledAsFlow(): Flow<List<UserFilter>> = dao.getAllEnabledAsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAll(): List<UserFilter> = dao.getAll().map { it.toDomain() }

    override fun getByIdAsFlow(id: Long): Flow<UserFilter?> = dao.getByIdAsFlow(id).map { it?.toDomain() }

    override suspend fun getById(id: Long): UserFilter? = dao.getById(id)?.toDomain()

    override suspend fun insert(userFilter: UserFilter) = dao.insert(userFilter.toEntity())

    override suspend fun insert(items: List<UserFilter>) = dao.insert(items.map { it.toEntity() })

    override suspend fun update(userFilter: UserFilter) = dao.update(userFilter.toEntity())

    override suspend fun delete(userFilter: UserFilter) = dao.delete(userFilter.toEntity())

    override suspend fun deleteAll() = dao.deleteAll()
}
