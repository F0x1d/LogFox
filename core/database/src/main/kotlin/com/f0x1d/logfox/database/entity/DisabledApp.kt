package com.f0x1d.logfox.database.entity

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity
data class DisabledApp(
    @ColumnInfo(name = "package_name", index = true) val packageName: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
)

@Dao
interface DisabledAppDao {

    @Query("SELECT * FROM DisabledApp")
    suspend fun getAll(): List<DisabledApp>

    @Query("SELECT * FROM DisabledApp")
    fun getAllAsFlow(): Flow<List<DisabledApp>>

    @Query("SELECT * FROM DisabledApp WHERE id = :id")
    suspend fun getById(id: Long): DisabledApp?

    @Query("SELECT * FROM DisabledApp WHERE id = :id")
    fun getByIdAsFlow(id: Long): Flow<DisabledApp?>

    @Query("SELECT * FROM DisabledApp WHERE package_name = :packageName")
    suspend fun getByPackageName(packageName: String): DisabledApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DisabledApp)

    @Update
    suspend fun update(item: DisabledApp)

    @Delete
    suspend fun delete(item: DisabledApp)

    @Query("DELETE FROM DisabledApp WHERE package_name = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("DELETE FROM DisabledApp")
    suspend fun deleteAll()
}
