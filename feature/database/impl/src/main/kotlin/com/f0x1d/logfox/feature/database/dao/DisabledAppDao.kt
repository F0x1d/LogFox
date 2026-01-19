package com.f0x1d.logfox.feature.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.f0x1d.logfox.feature.database.entity.DisabledAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface DisabledAppDao {

    @Query("SELECT * FROM DisabledApp")
    suspend fun getAll(): List<DisabledAppEntity>

    @Query("SELECT * FROM DisabledApp")
    fun getAllAsFlow(): Flow<List<DisabledAppEntity>>

    @Query("SELECT * FROM DisabledApp WHERE id = :id")
    suspend fun getById(id: Long): DisabledAppEntity?

    @Query("SELECT * FROM DisabledApp WHERE id = :id")
    fun getByIdAsFlow(id: Long): Flow<DisabledAppEntity?>

    @Query("SELECT * FROM DisabledApp WHERE package_name = :packageName")
    suspend fun getByPackageName(packageName: String): DisabledAppEntity?

    @Query("SELECT * FROM DisabledApp WHERE package_name = :packageName")
    fun getByPackageNameAsFlow(packageName: String): Flow<DisabledAppEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DisabledAppEntity)

    @Update
    suspend fun update(item: DisabledAppEntity)

    @Delete
    suspend fun delete(item: DisabledAppEntity)

    @Query("DELETE FROM DisabledApp WHERE package_name = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("DELETE FROM DisabledApp")
    suspend fun deleteAll()
}
