package com.f0x1d.logfox.database.entity

import androidx.annotation.Keep
import androidx.room.*
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.utils.GsonSkip
import kotlinx.coroutines.flow.Flow

@Keep
@Entity
data class UserFilter(
    @ColumnInfo(name = "including") val including: Boolean = true,
    @ColumnInfo(name = "allowed_levels") val allowedLevels: List<LogLevel> = emptyList(),
    @ColumnInfo(name = "uid") val uid: String? = null,
    @ColumnInfo(name = "pid") val pid: String? = null,
    @ColumnInfo(name = "tid") val tid: String? = null,
    @ColumnInfo(name = "package_name") val packageName: String? = null,
    @ColumnInfo(name = "tag") val tag: String? = null,
    @ColumnInfo(name = "content") val content: String? = null,
    @ColumnInfo(name = "enabled") @GsonSkip val enabled: Boolean = true,
    @PrimaryKey(autoGenerate = true) @GsonSkip val id: Long = 0
)

@Dao
interface UserFilterDao {

    @Query("SELECT * FROM UserFilter")
    fun getAllAsFlow(): Flow<List<UserFilter>>

    @Query("SELECT * FROM UserFilter WHERE id = :id")
    fun get(id: Long): Flow<UserFilter?>

    @Insert
    suspend fun insert(userFilter: UserFilter)

    @Insert
    suspend fun insert(items: List<UserFilter>)

    @Update
    suspend fun update(userFilter: UserFilter)

    @Delete
    suspend fun delete(userFilter: UserFilter)

    @Query("DELETE FROM UserFilter")
    suspend fun deleteAll()
}

class AllowedLevelsConverter {

    @TypeConverter
    fun toAllowedLevels(data: String) = data.split(",").map { enumValues<LogLevel>()[it.toInt()] }

    @TypeConverter
    fun fromAllowedLevels(allowedLevels: List<LogLevel>) = allowedLevels.joinToString(",") { it.ordinal.toString() }
}