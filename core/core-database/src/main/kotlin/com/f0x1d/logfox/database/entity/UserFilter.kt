package com.f0x1d.logfox.database.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Update
import com.f0x1d.logfox.arch.annotations.GsonSkip
import com.f0x1d.logfox.model.Identifiable
import com.f0x1d.logfox.model.logline.LogLevel
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
    @PrimaryKey(autoGenerate = true) @GsonSkip override val id: Long = 0,
) : Identifiable

@Dao
interface UserFilterDao {

    @Query("SELECT * FROM UserFilter")
    fun getAllAsFlow(): Flow<List<UserFilter>>

    @Query("SELECT * FROM UserFilter")
    suspend fun getAll(): List<UserFilter>

    @Query("SELECT * FROM UserFilter WHERE id = :id")
    fun getByIdAsFlow(id: Long): Flow<UserFilter?>

    @Query("SELECT * FROM UserFilter WHERE id = :id")
    suspend fun getById(id: Long): UserFilter?

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
    fun toAllowedLevels(data: String): List<LogLevel> = when (data.isEmpty()) {
        true -> emptyList()

        else -> data.split(",").map {
            enumValues<LogLevel>()[it.toInt()]
        }
    }

    @TypeConverter
    fun fromAllowedLevels(allowedLevels: List<LogLevel>): String = allowedLevels.joinToString(",") {
        it.ordinal.toString()
    }
}
