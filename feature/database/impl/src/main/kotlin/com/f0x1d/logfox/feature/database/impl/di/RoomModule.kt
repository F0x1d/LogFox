package com.f0x1d.logfox.feature.database.impl.di

import android.content.Context
import androidx.room.Room
import com.f0x1d.logfox.feature.database.impl.AppDatabase
import com.f0x1d.logfox.feature.database.impl.data.dao.AppCrashDao
import com.f0x1d.logfox.feature.database.impl.data.dao.DisabledAppDao
import com.f0x1d.logfox.feature.database.impl.data.dao.LogRecordingDao
import com.f0x1d.logfox.feature.database.impl.data.dao.UserFilterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext ctx: Context): AppDatabase = Room
        .databaseBuilder(ctx, AppDatabase::class.java, "main_database")
        .addMigrations(
            AppDatabase.MIGRATION_2_3,
            AppDatabase.MIGRATION_3_4,
            AppDatabase.MIGRATION_4_5,
            AppDatabase.MIGRATION_5_6,
            AppDatabase.MIGRATION_6_7,
            AppDatabase.MIGRATION_7_8,
            AppDatabase.MIGRATION_8_9,
            AppDatabase.MIGRATION_9_10,
            AppDatabase.MIGRATION_10_11,
            AppDatabase.MIGRATION_11_12,
        ).build()

    @Provides
    fun provideAppCrashDao(database: AppDatabase): AppCrashDao = database.appCrashes()

    @Provides
    fun provideLogRecordingDao(database: AppDatabase): LogRecordingDao = database.logRecordings()

    @Provides
    fun provideUserFilterDao(database: AppDatabase): UserFilterDao = database.userFilters()

    @Provides
    fun provideDisabledAppDao(database: AppDatabase): DisabledAppDao = database.disabledApps()
}
