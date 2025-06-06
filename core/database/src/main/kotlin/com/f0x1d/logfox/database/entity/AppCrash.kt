package com.f0x1d.logfox.database.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
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
    @ColumnInfo(name = "log") @Deprecated("Use logFile") val log: String = "",
    @ColumnInfo(name = "log_file") val logFile: File? = null,
    @ColumnInfo(name = "log_dump_file") val logDumpFile: File? = null,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Boolean = false,
    @ColumnInfo(name = "deleted_time") val deletedTime: Long? = null,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) {
    val notificationId get() = (if (id == 0L) dateAndTime else id).toInt()

    fun deleteLogFile() = logFile?.delete()
    fun deleteDumpFile() = logDumpFile?.delete()

    fun deleteAssociatedFiles() {
        deleteLogFile()
        deleteDumpFile()
    }
}

@Dao
interface AppCrashDao {

    private companion object {
        private const val DAYS_30 = 30L * 24 * 3600 * 1000
    }

    @Query("SELECT * FROM AppCrash WHERE is_deleted = :deleted ORDER BY date_and_time DESC")
    fun getAllAsFlow(deleted: Boolean = false): Flow<List<AppCrash>>

    @Query("SELECT * FROM AppCrash WHERE is_deleted = :deleted ORDER BY date_and_time DESC")
    suspend fun getAll(deleted: Boolean = false): List<AppCrash>

    @Query("SELECT * FROM AppCrash WHERE id = :id")
    fun getByIdAsFlow(id: Long): Flow<AppCrash?>

    @Query("SELECT * FROM AppCrash WHERE id = :id")
    suspend fun getById(id: Long): AppCrash?

    @Query("SELECT * FROM AppCrash WHERE package_name = :packageName AND is_deleted = 0")
    suspend fun getAllByPackageName(packageName: String): List<AppCrash>

    // This includes deleted ones as it will help to skip them
    @Query("SELECT * FROM AppCrash WHERE date_and_time = :dateAndTime AND package_name = :packageName")
    suspend fun getAllByDateAndTime(dateAndTime: Long, packageName: String): List<AppCrash>

    @Insert
    suspend fun insert(appCrash: AppCrash): Long

    @Update
    suspend fun update(appCrash: AppCrash)

    @Update
    suspend fun update(appCrashes: List<AppCrash>)

    suspend fun delete(appCrash: AppCrash) {
        update(appCrash.copy(isDeleted = true, deletedTime = System.currentTimeMillis()))
    }

    @Query("UPDATE AppCrash SET is_deleted = 1, deleted_time = :time WHERE package_name = :packageName")
    suspend fun deleteByPackageName(
        packageName: String,
        time: Long = System.currentTimeMillis()
    )

    @Query("UPDATE AppCrash SET is_deleted = 1, deleted_time = :time")
    suspend fun deleteAll(time: Long = System.currentTimeMillis())

    @Transaction
    suspend fun clearIfNeeded() {
        val itemsToDelete = getAll(deleted = true).filter {
            (System.currentTimeMillis() - (it.deletedTime ?: 0)) >= DAYS_30
        }.also {
            if (it.isEmpty()) return
        }

        _delete(itemsToDelete)
    }

    @Delete
    suspend fun _delete(crashes: List<AppCrash>)
}

@Keep
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

class FileConverter {
    @TypeConverter
    fun toFile(value: String?) = value?.let { File(it) }

    @TypeConverter
    fun fromFile(value: File) = value.absolutePath
}

data class AppCrashesCount(
    val lastCrash: AppCrash,
    val count: Int = 1,
)
