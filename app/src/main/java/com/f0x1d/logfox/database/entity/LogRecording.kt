package com.f0x1d.logfox.database.entity

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.io.File

@Entity
data class LogRecording(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "date_and_time") val dateAndTime: Long,
    @ColumnInfo(name = "file") val file: File,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {
    fun deleteFile() = file.delete()
}

@Dao
interface LogRecordingDao {

    @Query("SELECT * FROM LogRecording ORDER BY date_and_time DESC")
    suspend fun getAll(): List<LogRecording>

    @Query("SELECT * FROM LogRecording ORDER BY date_and_time DESC")
    fun getAllAsFlow(): Flow<List<LogRecording>>

    @Query("SELECT COUNT(*) FROM LogRecording")
    suspend fun count(): Int

    @Query("SELECT * FROM LogRecording WHERE id = :id")
    fun get(id: Long): Flow<LogRecording?>

    @Insert
    suspend fun insert(logRecording: LogRecording): Long

    @Update
    suspend fun update(logRecording: LogRecording)

    @Delete
    suspend fun delete(logRecording: LogRecording)

    @Query("DELETE FROM LogRecording")
    suspend fun deleteAll()
}