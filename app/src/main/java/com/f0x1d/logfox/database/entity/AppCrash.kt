package com.f0x1d.logfox.database.entity

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.io.File

@Entity
data class AppCrash(
    @ColumnInfo(name = "app_name") val appName: String?,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "crash_type") val crashType: CrashType,
    @ColumnInfo(name = "date_and_time", index = true) val dateAndTime: Long,
    @ColumnInfo(name = "log") val log: String,
    @ColumnInfo(name = "log_dump_file") val logDumpFile: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {
    val notificationId get() = (if (id == 0L) dateAndTime else id).toInt()

    fun deleteDumpFile() = logDumpFile?.let { File(it).delete() }
}

@Dao
interface AppCrashDao {

    @Query("SELECT * FROM AppCrash ORDER BY date_and_time DESC")
    fun getAllAsFlow(): Flow<List<AppCrash>>

    @Query("SELECT * FROM AppCrash ORDER BY date_and_time DESC")
    suspend fun getAll(): List<AppCrash>

    @Query("SELECT * FROM AppCrash WHERE package_name = :packageName")
    suspend fun getAllByPackageName(packageName: String): List<AppCrash>

    @Query("SELECT * FROM AppCrash WHERE date_and_time = :dateAndTime")
    suspend fun getAllByDateAndTime(dateAndTime: Long): List<AppCrash>

    @Query("SELECT * FROM AppCrash WHERE id = :id")
    fun get(id: Long): Flow<AppCrash?>

    @Insert
    suspend fun insert(appCrash: AppCrash): Long

    @Update
    suspend fun update(appCrash: AppCrash)

    @Delete
    suspend fun delete(appCrash: AppCrash)

    @Query("DELETE FROM AppCrash WHERE package_name = :packageName")
    suspend fun deleteByPackageName(packageName: String)

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