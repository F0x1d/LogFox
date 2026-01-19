package com.f0x1d.logfox.feature.preferences.impl.di

import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.NotificationsSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.ServiceSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.UISettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.GetLogsDisplayLimitUseCase
import com.f0x1d.logfox.feature.preferences.domain.GetLogsUpdateIntervalUseCase
import com.f0x1d.logfox.feature.preferences.domain.GetSelectedTerminalTypeUseCase
import com.f0x1d.logfox.feature.preferences.domain.GetStartOnBootUseCase
import com.f0x1d.logfox.feature.preferences.domain.ShouldFallbackToDefaultTerminalUseCase
import com.f0x1d.logfox.feature.preferences.impl.data.crashes.CrashesSettingsLocalDataSource
import com.f0x1d.logfox.feature.preferences.impl.data.crashes.CrashesSettingsLocalDataSourceImpl
import com.f0x1d.logfox.feature.preferences.impl.data.crashes.CrashesSettingsRepositoryImpl
import com.f0x1d.logfox.feature.preferences.impl.data.datetime.DateTimeSettingsLocalDataSource
import com.f0x1d.logfox.feature.preferences.impl.data.datetime.DateTimeSettingsLocalDataSourceImpl
import com.f0x1d.logfox.feature.preferences.impl.data.datetime.DateTimeSettingsRepositoryImpl
import com.f0x1d.logfox.feature.preferences.impl.data.logs.LogsSettingsLocalDataSource
import com.f0x1d.logfox.feature.preferences.impl.data.logs.LogsSettingsLocalDataSourceImpl
import com.f0x1d.logfox.feature.preferences.impl.data.logs.LogsSettingsRepositoryImpl
import com.f0x1d.logfox.feature.preferences.impl.data.notifications.NotificationsSettingsLocalDataSource
import com.f0x1d.logfox.feature.preferences.impl.data.notifications.NotificationsSettingsLocalDataSourceImpl
import com.f0x1d.logfox.feature.preferences.impl.data.notifications.NotificationsSettingsRepositoryImpl
import com.f0x1d.logfox.feature.preferences.impl.data.service.ServiceSettingsLocalDataSource
import com.f0x1d.logfox.feature.preferences.impl.data.service.ServiceSettingsLocalDataSourceImpl
import com.f0x1d.logfox.feature.preferences.impl.data.service.ServiceSettingsRepositoryImpl
import com.f0x1d.logfox.feature.preferences.impl.data.terminal.TerminalSettingsLocalDataSource
import com.f0x1d.logfox.feature.preferences.impl.data.terminal.TerminalSettingsLocalDataSourceImpl
import com.f0x1d.logfox.feature.preferences.impl.data.terminal.TerminalSettingsRepositoryImpl
import com.f0x1d.logfox.feature.preferences.impl.data.ui.UISettingsLocalDataSource
import com.f0x1d.logfox.feature.preferences.impl.data.ui.UISettingsLocalDataSourceImpl
import com.f0x1d.logfox.feature.preferences.impl.data.ui.UISettingsRepositoryImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.GetLogsDisplayLimitUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.GetLogsUpdateIntervalUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.GetSelectedTerminalTypeUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.GetStartOnBootUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.ShouldFallbackToDefaultTerminalUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface PreferencesModule {
    // UI Settings
    @Binds
    @Singleton
    fun bindUISettingsLocalDataSource(
        impl: UISettingsLocalDataSourceImpl,
    ): UISettingsLocalDataSource

    @Binds
    @Singleton
    fun bindUISettingsRepository(impl: UISettingsRepositoryImpl): UISettingsRepository

    // Logs Settings
    @Binds
    @Singleton
    fun bindLogsSettingsLocalDataSource(
        impl: LogsSettingsLocalDataSourceImpl,
    ): LogsSettingsLocalDataSource

    @Binds
    @Singleton
    fun bindLogsSettingsRepository(impl: LogsSettingsRepositoryImpl): LogsSettingsRepository

    // DateTime Settings
    @Binds
    @Singleton
    fun bindDateTimeSettingsLocalDataSource(
        impl: DateTimeSettingsLocalDataSourceImpl,
    ): DateTimeSettingsLocalDataSource

    @Binds
    @Singleton
    fun bindDateTimeSettingsRepository(
        impl: DateTimeSettingsRepositoryImpl,
    ): DateTimeSettingsRepository

    // Terminal Settings
    @Binds
    @Singleton
    fun bindTerminalSettingsLocalDataSource(
        impl: TerminalSettingsLocalDataSourceImpl,
    ): TerminalSettingsLocalDataSource

    @Binds
    @Singleton
    fun bindTerminalSettingsRepository(
        impl: TerminalSettingsRepositoryImpl,
    ): TerminalSettingsRepository

    // Crashes Settings
    @Binds
    @Singleton
    fun bindCrashesSettingsLocalDataSource(
        impl: CrashesSettingsLocalDataSourceImpl,
    ): CrashesSettingsLocalDataSource

    @Binds
    @Singleton
    fun bindCrashesSettingsRepository(
        impl: CrashesSettingsRepositoryImpl,
    ): CrashesSettingsRepository

    // Service Settings
    @Binds
    @Singleton
    fun bindServiceSettingsLocalDataSource(
        impl: ServiceSettingsLocalDataSourceImpl,
    ): ServiceSettingsLocalDataSource

    @Binds
    @Singleton
    fun bindServiceSettingsRepository(
        impl: ServiceSettingsRepositoryImpl,
    ): ServiceSettingsRepository

    // Notifications Settings
    @Binds
    @Singleton
    fun bindNotificationsSettingsLocalDataSource(
        impl: NotificationsSettingsLocalDataSourceImpl,
    ): NotificationsSettingsLocalDataSource

    @Binds
    @Singleton
    fun bindNotificationsSettingsRepository(
        impl: NotificationsSettingsRepositoryImpl,
    ): NotificationsSettingsRepository

    // Use Cases
    @Binds
    fun bindGetLogsUpdateIntervalUseCase(
        impl: GetLogsUpdateIntervalUseCaseImpl,
    ): GetLogsUpdateIntervalUseCase

    @Binds
    fun bindGetLogsDisplayLimitUseCase(
        impl: GetLogsDisplayLimitUseCaseImpl,
    ): GetLogsDisplayLimitUseCase

    @Binds
    fun bindShouldFallbackToDefaultTerminalUseCase(
        impl: ShouldFallbackToDefaultTerminalUseCaseImpl,
    ): ShouldFallbackToDefaultTerminalUseCase

    @Binds
    fun bindGetStartOnBootUseCase(impl: GetStartOnBootUseCaseImpl): GetStartOnBootUseCase

    @Binds
    fun bindGetSelectedTerminalTypeUseCase(
        impl: GetSelectedTerminalTypeUseCaseImpl,
    ): GetSelectedTerminalTypeUseCase
}
