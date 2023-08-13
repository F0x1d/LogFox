package com.f0x1d.logfox.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
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

@Database(entities = [AppCrash::class, LogRecording::class, UserFilter::class], version = 9)
@TypeConverters(CrashTypeConverter::class, AllowedLevelsConverter::class)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        val MIGRATION_2_3 = Migration(2, 3) {
            it.execSQL("CREATE TABLE LogRecording(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, date_and_time INTEGER NOT NULL, log TEXT NOT NULL)")
        }
        val MIGRATION_3_4 = Migration(3, 4) {
            it.execSQL("CREATE TABLE UserFilter(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, " +
                    "allowed_levels TEXT NOT NULL, pid TEXT, tid TEXT, tag TEXT, content TEXT)")
        }
        val MIGRATION_4_5 = Migration(4, 5) {
            it.execSQL("ALTER TABLE UserFilter ADD COLUMN enabled INTEGER NOT NULL DEFAULT 1")
        }
        val MIGRATION_5_6 = Migration(5, 6) {
            it.execSQL("DROP TABLE LogRecording")
            it.execSQL("CREATE TABLE LogRecording(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, date_and_time INTEGER NOT NULL, file TEXT NOT NULL)")
        }
        val MIGRATION_6_7 = Migration(6, 7) {
            val recordingString = LogFoxApp.instance.getString(R.string.record_file)
            it.execSQL("ALTER TABLE LogRecording ADD COLUMN title TEXT NOT NULL DEFAULT '${recordingString}'")
        }
        val MIGRATION_7_8 = Migration(7, 8) {
            it.execSQL("ALTER TABLE UserFilter ADD COLUMN including INTEGER NOT NULL DEFAULT 1")
        }
        val MIGRATION_8_9 = Migration(8, 9) {
            it.execSQL("ALTER TABLE UserFilter ADD COLUMN uid TEXT")
            it.execSQL("ALTER TABLE UserFilter ADD COLUMN package_name TEXT")
        }
    }

    abstract fun appCrashDao(): AppCrashDao
    abstract fun logRecordingDao(): LogRecordingDao
    abstract fun userFilterDao(): UserFilterDao
}