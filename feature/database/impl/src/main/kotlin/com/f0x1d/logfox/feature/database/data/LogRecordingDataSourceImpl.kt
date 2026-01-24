package com.f0x1d.logfox.feature.database.data

import com.f0x1d.logfox.feature.database.data.dao.LogRecordingDao
import com.f0x1d.logfox.feature.database.entity.LogRecordingEntity
import com.f0x1d.logfox.feature.database.mapper.toData
import com.f0x1d.logfox.feature.database.mapper.toRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LogRecordingDataSourceImpl @Inject constructor(
    private val dao: LogRecordingDao,
) : LogRecordingDataSource {

    override suspend fun getAll(cached: Boolean): List<LogRecordingEntity> =
        dao.getAll(cached).map { it.toData() }

    override fun getAllAsFlow(cached: Boolean): Flow<List<LogRecordingEntity>> =
        dao.getAllAsFlow(cached).map { list -> list.map { it.toData() } }

    override suspend fun getById(id: Long): LogRecordingEntity? = dao.getById(id)?.toData()

    override fun getByIdAsFlow(id: Long): Flow<LogRecordingEntity?> =
        dao.getByIdAsFlow(id).map { it?.toData() }

    override suspend fun count(cached: Boolean): Int = dao.count(cached)

    override suspend fun insert(logRecording: LogRecordingEntity): Long = dao.insert(logRecording.toRoom())

    override suspend fun update(logRecording: LogRecordingEntity) = dao.update(logRecording.toRoom())

    override suspend fun delete(logRecording: LogRecordingEntity) = dao.delete(logRecording.toRoom())

    override suspend fun deleteAll() = dao.deleteAll()
}
