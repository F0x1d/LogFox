package com.f0x1d.logfox.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AppCrash::class], version = 2)
@TypeConverters(CrashTypeConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun appCrashDao(): AppCrashDao
}