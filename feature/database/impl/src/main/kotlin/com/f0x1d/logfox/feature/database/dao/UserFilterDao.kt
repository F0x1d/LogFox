package com.f0x1d.logfox.feature.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.f0x1d.logfox.feature.database.entity.UserFilterEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface UserFilterDao {

    @Query("SELECT * FROM UserFilter")
    fun getAllAsFlow(): Flow<List<UserFilterEntity>>

    @Query("SELECT * FROM UserFilter WHERE enabled = 1")
    fun getAllEnabledAsFlow(): Flow<List<UserFilterEntity>>

    @Query("SELECT * FROM UserFilter")
    suspend fun getAll(): List<UserFilterEntity>

    @Query("SELECT * FROM UserFilter WHERE id = :id")
    fun getByIdAsFlow(id: Long): Flow<UserFilterEntity?>

    @Query("SELECT * FROM UserFilter WHERE id = :id")
    suspend fun getById(id: Long): UserFilterEntity?

    @Insert
    suspend fun insert(userFilter: UserFilterEntity)

    @Insert
    suspend fun insert(items: List<UserFilterEntity>)

    @Update
    suspend fun update(userFilter: UserFilterEntity)

    @Delete
    suspend fun delete(userFilter: UserFilterEntity)

    @Query("DELETE FROM UserFilter")
    suspend fun deleteAll()
}
