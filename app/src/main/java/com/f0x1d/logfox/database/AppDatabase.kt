package com.f0x1d.logfox.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [AppCrash::class, LogRecording::class], version = 3)
@TypeConverters(CrashTypeConverter::class)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE LogRecording(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, date_and_time INTEGER NOT NULL, log TEXT NOT NULL)")
            }
        }
    }

    abstract fun appCrashDao(): AppCrashDao
    abstract fun logRecordingDao(): LogRecordingDao
}