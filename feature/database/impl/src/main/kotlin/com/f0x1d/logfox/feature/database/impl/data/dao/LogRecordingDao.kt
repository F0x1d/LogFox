package com.f0x1d.logfox.feature.database.impl.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.f0x1d.logfox.feature.database.impl.entity.LogRecordingRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LogRecordingDao {

    @Query(
        "SELECT * FROM LogRecording WHERE is_cache_recording = :cached ORDER BY date_and_time DESC",
    )
    suspend fun getAll(cached: Boolean = false): List<LogRecordingRoomEntity>

    @Query(
        "SELECT * FROM LogRecording WHERE is_cache_recording = :cached ORDER BY date_and_time DESC",
    )
    fun getAllAsFlow(cached: Boolean = false): Flow<List<LogRecordingRoomEntity>>

    @Query("SELECT * FROM LogRecording WHERE id = :id")
    suspend fun getById(id: Long): LogRecordingRoomEntity?

    @Query("SELECT * FROM LogRecording WHERE id = :id")
    fun getByIdAsFlow(id: Long): Flow<LogRecordingRoomEntity?>

    @Query("SELECT COUNT(*) FROM LogRecording WHERE is_cache_recording = :cached")
    suspend fun count(cached: Boolean = false): Int

    @Insert
    suspend fun insert(logRecording: LogRecordingRoomEntity): Long

    @Update
    suspend fun update(logRecording: LogRecordingRoomEntity)

    @Delete
    suspend fun delete(logRecording: LogRecordingRoomEntity)

    @Query("DELETE FROM LogRecording")
    suspend fun deleteAll()
}
