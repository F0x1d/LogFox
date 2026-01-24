package com.f0x1d.logfox.feature.database.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.f0x1d.logfox.feature.database.entity.DisabledAppRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface DisabledAppDao {

    @Query("SELECT * FROM DisabledApp")
    suspend fun getAll(): List<DisabledAppRoomEntity>

    @Query("SELECT * FROM DisabledApp")
    fun getAllAsFlow(): Flow<List<DisabledAppRoomEntity>>

    @Query("SELECT * FROM DisabledApp WHERE id = :id")
    suspend fun getById(id: Long): DisabledAppRoomEntity?

    @Query("SELECT * FROM DisabledApp WHERE id = :id")
    fun getByIdAsFlow(id: Long): Flow<DisabledAppRoomEntity?>

    @Query("SELECT * FROM DisabledApp WHERE package_name = :packageName")
    suspend fun getByPackageName(packageName: String): DisabledAppRoomEntity?

    @Query("SELECT * FROM DisabledApp WHERE package_name = :packageName")
    fun getByPackageNameAsFlow(packageName: String): Flow<DisabledAppRoomEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DisabledAppRoomEntity)

    @Update
    suspend fun update(item: DisabledAppRoomEntity)

    @Delete
    suspend fun delete(item: DisabledAppRoomEntity)

    @Query("DELETE FROM DisabledApp WHERE package_name = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("DELETE FROM DisabledApp")
    suspend fun deleteAll()
}
