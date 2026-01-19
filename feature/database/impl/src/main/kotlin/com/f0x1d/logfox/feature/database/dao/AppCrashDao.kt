package com.f0x1d.logfox.feature.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.f0x1d.logfox.feature.database.entity.AppCrashEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AppCrashDao {

    private companion object {
        private const val DAYS_30 = 30L * 24 * 3600 * 1000
    }

    @Query("SELECT * FROM AppCrash WHERE is_deleted = :deleted ORDER BY date_and_time DESC")
    fun getAllAsFlow(deleted: Boolean = false): Flow<List<AppCrashEntity>>

    @Query("SELECT * FROM AppCrash WHERE is_deleted = :deleted ORDER BY date_and_time DESC")
    suspend fun getAll(deleted: Boolean = false): List<AppCrashEntity>

    @Query("SELECT * FROM AppCrash WHERE id = :id")
    fun getByIdAsFlow(id: Long): Flow<AppCrashEntity?>

    @Query("SELECT * FROM AppCrash WHERE id = :id")
    suspend fun getById(id: Long): AppCrashEntity?

    @Query("SELECT * FROM AppCrash WHERE package_name = :packageName AND is_deleted = 0")
    suspend fun getAllByPackageName(packageName: String): List<AppCrashEntity>

    // This includes deleted ones as it will help to skip them
    @Query("SELECT * FROM AppCrash WHERE date_and_time = :dateAndTime AND package_name = :packageName")
    suspend fun getAllByDateAndTime(dateAndTime: Long, packageName: String): List<AppCrashEntity>

    @Insert
    suspend fun insert(appCrash: AppCrashEntity): Long

    @Update
    suspend fun update(appCrash: AppCrashEntity)

    @Update
    suspend fun update(appCrashes: List<AppCrashEntity>)

    suspend fun delete(appCrash: AppCrashEntity) {
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
    suspend fun _delete(crashes: List<AppCrashEntity>)
}
