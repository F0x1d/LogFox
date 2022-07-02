package com.f0x1d.logfox.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [AppCrash::class, LogRecording::class, UserFilter::class], version = 4)
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
    }

    abstract fun appCrashDao(): AppCrashDao
    abstract fun logRecordingDao(): LogRecordingDao
    abstract fun userFilterDao(): UserFilterDao
}