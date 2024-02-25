package com.f0x1d.logfox.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.entity.AllowedLevelsConverter
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.AppCrashDao
import com.f0x1d.logfox.database.entity.CrashTypeConverter
import com.f0x1d.logfox.database.entity.FileConverter
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.database.entity.LogRecordingDao
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.database.entity.UserFilterDao

@Database(
    entities = [
        AppCrash::class,
        LogRecording::class,
        UserFilter::class
    ],
    version = 15,
    autoMigrations = [
        AutoMigration(
            from = 12,
            to = 13,
            spec = AppDatabase.Companion.AutoMigration12_13::class
        ),
        AutoMigration(
            from = 13,
            to = 14
        ),
        AutoMigration(
            from = 14,
            to = 15
        )
    ]
)
@TypeConverters(
    CrashTypeConverter::class,
    AllowedLevelsConverter::class,
    FileConverter::class
)
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
        val MIGRATION_9_10 = Migration(9, 10) {
            it.execSQL("ALTER TABLE AppCrash ADD COLUMN log_dump TEXT")
        }
        val MIGRATION_10_11 = Migration(10, 11) {
            it.execSQL("ALTER TABLE AppCrash ADD COLUMN log_dump_file TEXT")
        }
        val MIGRATION_11_12 = Migration(11, 12) {
            it.execSQL("CREATE INDEX index_AppCrash_date_and_time ON AppCrash(date_and_time)")
        }
        @DeleteColumn(tableName = "AppCrash", columnName = "log_dump")
        class AutoMigration12_13: AutoMigrationSpec
        /*val MIGRATION_13_14 = Migration(13, 14) {
            it.execSQL("ALTER TABLE AppCrash ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0")
            it.execSQL("ALTER TABLE AppCrash ADD COLUMN deleted_time INTEGER")
        }*/
    }

    abstract fun appCrashDao(): AppCrashDao
    abstract fun logRecordingDao(): LogRecordingDao
    abstract fun userFilterDao(): UserFilterDao
}