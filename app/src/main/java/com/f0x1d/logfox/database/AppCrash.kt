package com.f0x1d.logfox.database

import androidx.room.*

@Entity
data class AppCrash(@ColumnInfo(name = "app_name") val appName: String?,
                    @ColumnInfo(name = "package_name") val packageName: String,
                    @ColumnInfo(name = "crash_type") val crashType: CrashType,
                    @ColumnInfo(name = "date_and_time") val dateAndTime: Long,
                    @ColumnInfo(name = "log") val log: String,
                    @PrimaryKey(autoGenerate = true) val id: Long = 0)

@Dao
interface AppCrashDao {

    @Query("SELECT * FROM AppCrash ORDER BY date_and_time DESC")
    fun getAll(): List<AppCrash>

    @Insert
    fun insert(appCrash: AppCrash)

    @Query("DELETE FROM AppCrash")
    fun deleteAll()
}

enum class CrashType(val readableName: String) {
    JAVA("Java"), JNI("JNI"), ANR("ANR")
}

class CrashTypeConverter {
    @TypeConverter
    fun toCrashType(value: Int) = enumValues<CrashType>()[value]

    @TypeConverter
    fun fromCrashType(value: CrashType) = value.ordinal
}