package com.f0x1d.logfox.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.io.File

@Entity
data class LogRecording(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "date_and_time") val dateAndTime: Long,
    @ColumnInfo(name = "file") val file: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {
    fun deleteFile() = File(file).delete()
}

@Dao
interface LogRecordingDao {

    @Query("SELECT * FROM LogRecording ORDER BY date_and_time DESC")
    suspend fun getAll(): List<LogRecording>

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