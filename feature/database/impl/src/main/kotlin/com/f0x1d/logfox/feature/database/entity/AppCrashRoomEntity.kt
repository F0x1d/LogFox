package com.f0x1d.logfox.feature.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.File

@Entity(tableName = "AppCrash")
internal data class AppCrashRoomEntity(
    @ColumnInfo(name = "app_name") val appName: String?,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "crash_type") val crashType: CrashType,
    @ColumnInfo(name = "date_and_time", index = true) val dateAndTime: Long,
    @ColumnInfo(name = "log") @Deprecated("Use logFile") val log: String = "",
    @ColumnInfo(name = "log_file") val logFile: File? = null,
    @ColumnInfo(name = "log_dump_file") val logDumpFile: File? = null,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Boolean = false,
    @ColumnInfo(name = "deleted_time") val deletedTime: Long? = null,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
)

internal class CrashTypeConverter {
    @TypeConverter
    fun toCrashType(value: Int) = enumValues<CrashType>()[value]

    @TypeConverter
    fun fromCrashType(value: CrashType) = value.ordinal
}
