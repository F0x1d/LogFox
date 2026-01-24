package com.f0x1d.logfox.feature.database.data

import com.f0x1d.logfox.feature.database.data.dao.AppCrashDao
import com.f0x1d.logfox.feature.database.entity.AppCrashEntity
import com.f0x1d.logfox.feature.database.mapper.toData
import com.f0x1d.logfox.feature.database.mapper.toRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AppCrashDataSourceImpl @Inject constructor(
    private val dao: AppCrashDao,
) : AppCrashDataSource {

    override fun getAllAsFlow(deleted: Boolean): Flow<List<AppCrashEntity>> =
        dao.getAllAsFlow(deleted).map { list -> list.map { it.toData() } }

    override suspend fun getAll(deleted: Boolean): List<AppCrashEntity> =
        dao.getAll(deleted).map { it.toData() }

    override fun getByIdAsFlow(id: Long): Flow<AppCrashEntity?> =
        dao.getByIdAsFlow(id).map { it?.toData() }

    override suspend fun getById(id: Long): AppCrashEntity? = dao.getById(id)?.toData()

    override suspend fun getAllByPackageName(packageName: String): List<AppCrashEntity> =
        dao.getAllByPackageName(packageName).map { it.toData() }

    override suspend fun getAllByDateAndTime(dateAndTime: Long, packageName: String): List<AppCrashEntity> =
        dao.getAllByDateAndTime(dateAndTime, packageName).map { it.toData() }

    override suspend fun insert(appCrash: AppCrashEntity): Long = dao.insert(appCrash.toRoom())

    override suspend fun update(appCrash: AppCrashEntity) = dao.update(appCrash.toRoom())

    override suspend fun delete(appCrash: AppCrashEntity) = dao.delete(appCrash.toRoom())

    override suspend fun deleteByPackageName(packageName: String, time: Long) =
        dao.deleteByPackageName(packageName, time)

    override suspend fun deleteAll(time: Long) = dao.deleteAll(time)

    override suspend fun clearIfNeeded() = dao.clearIfNeeded()
}
