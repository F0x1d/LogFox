package com.f0x1d.logfox.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class AppCrash(
    @ColumnInfo(name = "app_name") val appName: String?,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "crash_type") val crashType: CrashType,
    @ColumnInfo(name = "date_and_time") val dateAndTime: Long,
    @ColumnInfo(name = "log") val log: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {
    val notificationId get() = (if (id == 0L) dateAndTime else id).toInt()
}

@Dao
interface AppCrashDao {

    @Query("SELECT * FROM AppCrash ORDER BY date_and_time DESC")
    suspend fun getAll(): List<AppCrash>

    @Query("SELECT * FROM AppCrash WHERE id = :id")
    fun get(id: Long): Flow<AppCrash?>

    @Insert
    suspend fun insert(appCrash: AppCrash): Long

    @Delete
    suspend fun delete(appCrash: AppCrash)

    @Query("DELETE FROM AppCrash")
    suspend fun deleteAll()
}

enum class CrashType(val readableName: String) {
    JAVA("Java"),
    JNI("JNI"),
    ANR("ANR")
}

class CrashTypeConverter {
    @TypeConverter
    fun toCrashType(value: Int) = enumValues<CrashType>()[value]

    @TypeConverter
    fun fromCrashType(value: CrashType) = value.ordinal
}