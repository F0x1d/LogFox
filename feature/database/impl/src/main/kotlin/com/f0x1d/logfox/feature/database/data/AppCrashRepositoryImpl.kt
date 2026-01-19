package com.f0x1d.logfox.feature.database.data

import com.f0x1d.logfox.feature.database.dao.AppCrashDao
import com.f0x1d.logfox.feature.database.mapper.toDomain
import com.f0x1d.logfox.feature.database.mapper.toEntity
import com.f0x1d.logfox.feature.database.model.AppCrash
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AppCrashRepositoryImpl @Inject constructor(
    private val dao: AppCrashDao,
) : AppCrashRepository {

    override fun getAllAsFlow(): Flow<List<AppCrash>> =
        dao.getAllAsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAll(): List<AppCrash> =
        dao.getAll().map { it.toDomain() }

    override fun getByIdAsFlow(id: Long): Flow<AppCrash?> =
        dao.getByIdAsFlow(id).map { it?.toDomain() }

    override suspend fun getById(id: Long): AppCrash? =
        dao.getById(id)?.toDomain()

    override suspend fun getAllByPackageName(packageName: String): List<AppCrash> =
        dao.getAllByPackageName(packageName).map { it.toDomain() }

    override suspend fun getAllByDateAndTime(dateAndTime: Long, packageName: String): List<AppCrash> =
        dao.getAllByDateAndTime(dateAndTime, packageName).map { it.toDomain() }

    override suspend fun insert(appCrash: AppCrash): Long =
        dao.insert(appCrash.toEntity())

    override suspend fun update(appCrash: AppCrash) =
        dao.update(appCrash.toEntity())

    override suspend fun delete(appCrash: AppCrash) =
        dao.delete(appCrash.toEntity())

    override suspend fun deleteByPackageName(packageName: String, time: Long) =
        dao.deleteByPackageName(packageName, time)

    override suspend fun deleteAll(time: Long) =
        dao.deleteAll(time)

    override suspend fun clearIfNeeded() =
        dao.clearIfNeeded()
}
