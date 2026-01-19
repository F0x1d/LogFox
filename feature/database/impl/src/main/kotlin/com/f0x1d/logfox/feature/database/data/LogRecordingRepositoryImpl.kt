package com.f0x1d.logfox.feature.database.data

import com.f0x1d.logfox.feature.database.dao.LogRecordingDao
import com.f0x1d.logfox.feature.database.mapper.toDomain
import com.f0x1d.logfox.feature.database.mapper.toEntity
import com.f0x1d.logfox.feature.database.model.LogRecording
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LogRecordingRepositoryImpl @Inject constructor(
    private val dao: LogRecordingDao,
) : LogRecordingRepository {

    override suspend fun getAll(): List<LogRecording> =
        dao.getAll().map { it.toDomain() }

    override fun getAllAsFlow(): Flow<List<LogRecording>> =
        dao.getAllAsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): LogRecording? =
        dao.getById(id)?.toDomain()

    override fun getByIdAsFlow(id: Long): Flow<LogRecording?> =
        dao.getByIdAsFlow(id).map { it?.toDomain() }

    override suspend fun count(): Int =
        dao.count()

    override suspend fun insert(logRecording: LogRecording): Long =
        dao.insert(logRecording.toEntity())

    override suspend fun update(logRecording: LogRecording) =
        dao.update(logRecording.toEntity())

    override suspend fun delete(logRecording: LogRecording) =
        dao.delete(logRecording.toEntity())

    override suspend fun deleteAll() =
        dao.deleteAll()
}
