package com.f0x1d.logfox.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.entity.AllowedLevelsConverter
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.AppCrashDao
import com.f0x1d.logfox.database.entity.CrashTypeConverter
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.database.entity.LogRecordingDao
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.database.entity.UserFilterDao

@Database(entities = [AppCrash::class, LogRecording::class, UserFilter::class], version = 7)
@TypeConverters(CrashTypeConverter::class, AllowedLevelsConverter::class)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE LogRecording(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, date_and_time INTEGER NOT NULL, log TEXT NOT NULL)")
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE UserFilter(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, " +
                        "allowed_levels TEXT NOT NULL, pid TEXT, tid TEXT, tag TEXT, content TEXT)")
            }
        }
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE UserFilter ADD COLUMN enabled INTEGER NOT NULL DEFAULT 1")
            }
        }
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE LogRecording")
                database.execSQL("CREATE TABLE LogRecording(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, date_and_time INTEGER NOT NULL, file TEXT NOT NULL)")
            }
        }
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val recordingString = LogFoxApp.instance.getString(R.string.record_file)
                database.execSQL("ALTER TABLE LogRecording ADD COLUMN title TEXT NOT NULL DEFAULT '${recordingString}'")
            }
        }
    }

    abstract fun appCrashDao(): AppCrashDao
    abstract fun logRecordingDao(): LogRecordingDao
    abstract fun userFilterDao(): UserFilterDao
}