package com.f0x1d.logfox.feature.database.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.f0x1d.logfox.feature.database.entity.UserFilterRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface UserFilterDao {

    @Query("SELECT * FROM UserFilter")
    fun getAllAsFlow(): Flow<List<UserFilterRoomEntity>>

    @Query("SELECT * FROM UserFilter WHERE enabled = 1")
    fun getAllEnabledAsFlow(): Flow<List<UserFilterRoomEntity>>

    @Query("SELECT * FROM UserFilter")
    suspend fun getAll(): List<UserFilterRoomEntity>

    @Query("SELECT * FROM UserFilter WHERE id = :id")
    fun getByIdAsFlow(id: Long): Flow<UserFilterRoomEntity?>

    @Query("SELECT * FROM UserFilter WHERE id = :id")
    suspend fun getById(id: Long): UserFilterRoomEntity?

    @Insert
    suspend fun insert(userFilter: UserFilterRoomEntity)

    @Insert
    suspend fun insert(items: List<UserFilterRoomEntity>)

    @Update
    suspend fun update(userFilter: UserFilterRoomEntity)

    @Delete
    suspend fun delete(userFilter: UserFilterRoomEntity)

    @Query("DELETE FROM UserFilter")
    suspend fun deleteAll()
}
