package com.f0x1d.logfox.feature.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.File

@Entity(tableName = "LogRecording")
internal data class LogRecordingRoomEntity(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "date_and_time") val dateAndTime: Long,
    @ColumnInfo(name = "file") val file: File,
    @ColumnInfo(
        name = "is_cache_recording",
        defaultValue = "0",
    ) val isCacheRecording: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
)

internal class FileConverter {
    @TypeConverter
    fun toFile(value: String?) = value?.let { File(it) }

    @TypeConverter
    fun fromFile(value: File) = value.absolutePath
}
