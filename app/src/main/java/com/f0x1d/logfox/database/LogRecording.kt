package com.f0x1d.logfox.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.io.File

@Entity
data class LogRecording(@ColumnInfo(name = "date_and_time") val dateAndTime: Long,
                        @ColumnInfo(name = "file") val file: String,
                        @PrimaryKey(autoGenerate = true) val id: Long = 0) {

    fun deleteFile() = File(file).delete()
}

@Dao
interface LogRecordingDao {

    @Query("SELECT * FROM LogRecording ORDER BY date_and_time DESC")
    fun getAll(): List<LogRecording>

    @Query("SELECT * FROM LogRecording WHERE id = :id")
    fun get(id: Long): Flow<LogRecording?>

    @Insert
    fun insert(logRecording: LogRecording): Long

    @Delete
    fun delete(logRecording: LogRecording)

    @Query("DELETE FROM LogRecording")
    fun deleteAll()
}