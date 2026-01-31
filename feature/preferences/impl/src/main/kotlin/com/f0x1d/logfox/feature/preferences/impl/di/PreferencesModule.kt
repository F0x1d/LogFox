package com.f0x1d.logfox.feature.preferences.impl.di

import com.f0x1d.logfox.feature.preferences.api.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.data.LogsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.data.NotificationsSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.data.ServiceSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.data.UISettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetCrashesSortReversedOrderFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetCrashesSortTypeFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetOpenCrashesOnStartupUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetUseSeparateNotificationsChannelsForCrashesFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetUseSeparateNotificationsChannelsForCrashesUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetWrapCrashLogLinesFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.GetWrapCrashLogLinesUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.SetCrashesSortReversedOrderUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.crashes.SetCrashesSortTypeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.GetDateFormatFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.GetDateFormatUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.GetTimeFormatFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.GetTimeFormatUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.SetDateFormatUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.SetTimeFormatUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsDisplayLimitFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsDisplayLimitUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsExpandedFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsExpandedUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsTextSizeFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsTextSizeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsUpdateIntervalFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetLogsUpdateIntervalUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetResumeLoggingWithBottomTouchFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetResumeLoggingWithBottomTouchUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogContentFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogContentUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogDateFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogDateUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogPackageFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogPackageUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogPidFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogPidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTagFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTagUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTidFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTimeFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogTimeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogUidFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.GetShowLogUidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetLogsDisplayLimitUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetLogsTextSizeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetLogsUpdateIntervalUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogContentUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogDateUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogPackageUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogPidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogTagUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogTidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogTimeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.logs.SetShowLogUidUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.notifications.GetAskedNotificationsPermissionUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.notifications.SetAskedNotificationsPermissionUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.service.GetIncludeDeviceInfoInArchivesUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.service.GetStartOnBootUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.terminal.GetSelectedTerminalTypeFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.terminal.GetSelectedTerminalTypeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.terminal.SetSelectedTerminalTypeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.terminal.ShouldFallbackToDefaultTerminalUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.ui.GetNightThemeFlowUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.ui.GetNightThemeUseCase
import com.f0x1d.logfox.feature.preferences.api.domain.ui.SetNightThemeUseCase
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
import com.f0x1d.logfox.feature.preferences.impl.domain.crashes.GetCrashesSortReversedOrderFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.crashes.GetCrashesSortTypeFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.crashes.GetOpenCrashesOnStartupUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.crashes.GetUseSeparateNotificationsChannelsForCrashesFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.crashes.GetUseSeparateNotificationsChannelsForCrashesUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.crashes.GetWrapCrashLogLinesFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.crashes.GetWrapCrashLogLinesUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.crashes.SetCrashesSortReversedOrderUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.crashes.SetCrashesSortTypeUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.datetime.GetDateFormatFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.datetime.GetDateFormatUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.datetime.GetTimeFormatFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.datetime.GetTimeFormatUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.datetime.SetDateFormatUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.datetime.SetTimeFormatUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetLogsDisplayLimitFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetLogsDisplayLimitUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetLogsExpandedFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetLogsExpandedUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetLogsTextSizeFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetLogsTextSizeUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetLogsUpdateIntervalFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetLogsUpdateIntervalUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetResumeLoggingWithBottomTouchFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetResumeLoggingWithBottomTouchUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogContentFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogContentUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogDateFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogDateUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogPackageFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogPackageUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogPidFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogPidUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogTagFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogTagUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogTidFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogTidUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogTimeFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogTimeUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogUidFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.GetShowLogUidUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.SetLogsDisplayLimitUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.SetLogsTextSizeUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.SetLogsUpdateIntervalUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.SetShowLogContentUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.SetShowLogDateUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.SetShowLogPackageUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.SetShowLogPidUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.SetShowLogTagUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.SetShowLogTidUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.SetShowLogTimeUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.logs.SetShowLogUidUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.notifications.GetAskedNotificationsPermissionUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.notifications.SetAskedNotificationsPermissionUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.service.GetIncludeDeviceInfoInArchivesUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.service.GetStartOnBootUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.terminal.GetSelectedTerminalTypeFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.terminal.GetSelectedTerminalTypeUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.terminal.SetSelectedTerminalTypeUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.terminal.ShouldFallbackToDefaultTerminalUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.ui.GetNightThemeFlowUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.ui.GetNightThemeUseCaseImpl
import com.f0x1d.logfox.feature.preferences.impl.domain.ui.SetNightThemeUseCaseImpl
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

    // Logs Use Cases
    @Binds
    fun bindGetLogsUpdateIntervalUseCase(
        impl: GetLogsUpdateIntervalUseCaseImpl,
    ): GetLogsUpdateIntervalUseCase

    @Binds
    fun bindGetLogsDisplayLimitUseCase(
        impl: GetLogsDisplayLimitUseCaseImpl,
    ): GetLogsDisplayLimitUseCase

    @Binds
    fun bindGetResumeLoggingWithBottomTouchUseCase(
        impl: GetResumeLoggingWithBottomTouchUseCaseImpl,
    ): GetResumeLoggingWithBottomTouchUseCase

    @Binds
    fun bindGetLogsTextSizeUseCase(
        impl: GetLogsTextSizeUseCaseImpl,
    ): GetLogsTextSizeUseCase

    @Binds
    fun bindGetLogsExpandedUseCase(
        impl: GetLogsExpandedUseCaseImpl,
    ): GetLogsExpandedUseCase

    @Binds
    fun bindGetShowLogDateUseCase(
        impl: GetShowLogDateUseCaseImpl,
    ): GetShowLogDateUseCase

    @Binds
    fun bindGetShowLogDateFlowUseCase(
        impl: GetShowLogDateFlowUseCaseImpl,
    ): GetShowLogDateFlowUseCase

    @Binds
    fun bindSetShowLogDateUseCase(
        impl: SetShowLogDateUseCaseImpl,
    ): SetShowLogDateUseCase

    @Binds
    fun bindGetShowLogTimeUseCase(
        impl: GetShowLogTimeUseCaseImpl,
    ): GetShowLogTimeUseCase

    @Binds
    fun bindGetShowLogTimeFlowUseCase(
        impl: GetShowLogTimeFlowUseCaseImpl,
    ): GetShowLogTimeFlowUseCase

    @Binds
    fun bindSetShowLogTimeUseCase(
        impl: SetShowLogTimeUseCaseImpl,
    ): SetShowLogTimeUseCase

    @Binds
    fun bindGetShowLogUidUseCase(
        impl: GetShowLogUidUseCaseImpl,
    ): GetShowLogUidUseCase

    @Binds
    fun bindGetShowLogUidFlowUseCase(
        impl: GetShowLogUidFlowUseCaseImpl,
    ): GetShowLogUidFlowUseCase

    @Binds
    fun bindSetShowLogUidUseCase(
        impl: SetShowLogUidUseCaseImpl,
    ): SetShowLogUidUseCase

    @Binds
    fun bindGetShowLogPidUseCase(
        impl: GetShowLogPidUseCaseImpl,
    ): GetShowLogPidUseCase

    @Binds
    fun bindGetShowLogPidFlowUseCase(
        impl: GetShowLogPidFlowUseCaseImpl,
    ): GetShowLogPidFlowUseCase

    @Binds
    fun bindSetShowLogPidUseCase(
        impl: SetShowLogPidUseCaseImpl,
    ): SetShowLogPidUseCase

    @Binds
    fun bindGetShowLogTidUseCase(
        impl: GetShowLogTidUseCaseImpl,
    ): GetShowLogTidUseCase

    @Binds
    fun bindGetShowLogTidFlowUseCase(
        impl: GetShowLogTidFlowUseCaseImpl,
    ): GetShowLogTidFlowUseCase

    @Binds
    fun bindSetShowLogTidUseCase(
        impl: SetShowLogTidUseCaseImpl,
    ): SetShowLogTidUseCase

    @Binds
    fun bindGetShowLogPackageUseCase(
        impl: GetShowLogPackageUseCaseImpl,
    ): GetShowLogPackageUseCase

    @Binds
    fun bindGetShowLogPackageFlowUseCase(
        impl: GetShowLogPackageFlowUseCaseImpl,
    ): GetShowLogPackageFlowUseCase

    @Binds
    fun bindSetShowLogPackageUseCase(
        impl: SetShowLogPackageUseCaseImpl,
    ): SetShowLogPackageUseCase

    @Binds
    fun bindGetShowLogTagUseCase(
        impl: GetShowLogTagUseCaseImpl,
    ): GetShowLogTagUseCase

    @Binds
    fun bindGetShowLogTagFlowUseCase(
        impl: GetShowLogTagFlowUseCaseImpl,
    ): GetShowLogTagFlowUseCase

    @Binds
    fun bindSetShowLogTagUseCase(
        impl: SetShowLogTagUseCaseImpl,
    ): SetShowLogTagUseCase

    @Binds
    fun bindGetShowLogContentUseCase(
        impl: GetShowLogContentUseCaseImpl,
    ): GetShowLogContentUseCase

    @Binds
    fun bindGetShowLogContentFlowUseCase(
        impl: GetShowLogContentFlowUseCaseImpl,
    ): GetShowLogContentFlowUseCase

    @Binds
    fun bindSetShowLogContentUseCase(
        impl: SetShowLogContentUseCaseImpl,
    ): SetShowLogContentUseCase

    @Binds
    fun bindGetLogsUpdateIntervalFlowUseCase(
        impl: GetLogsUpdateIntervalFlowUseCaseImpl,
    ): GetLogsUpdateIntervalFlowUseCase

    @Binds
    fun bindSetLogsUpdateIntervalUseCase(
        impl: SetLogsUpdateIntervalUseCaseImpl,
    ): SetLogsUpdateIntervalUseCase

    @Binds
    fun bindGetLogsTextSizeFlowUseCase(
        impl: GetLogsTextSizeFlowUseCaseImpl,
    ): GetLogsTextSizeFlowUseCase

    @Binds
    fun bindSetLogsTextSizeUseCase(
        impl: SetLogsTextSizeUseCaseImpl,
    ): SetLogsTextSizeUseCase

    @Binds
    fun bindGetLogsDisplayLimitFlowUseCase(
        impl: GetLogsDisplayLimitFlowUseCaseImpl,
    ): GetLogsDisplayLimitFlowUseCase

    @Binds
    fun bindSetLogsDisplayLimitUseCase(
        impl: SetLogsDisplayLimitUseCaseImpl,
    ): SetLogsDisplayLimitUseCase

    @Binds
    fun bindGetResumeLoggingWithBottomTouchFlowUseCase(
        impl: GetResumeLoggingWithBottomTouchFlowUseCaseImpl,
    ): GetResumeLoggingWithBottomTouchFlowUseCase

    @Binds
    fun bindGetLogsExpandedFlowUseCase(
        impl: GetLogsExpandedFlowUseCaseImpl,
    ): GetLogsExpandedFlowUseCase

    // Terminal Use Cases
    @Binds
    fun bindShouldFallbackToDefaultTerminalUseCase(
        impl: ShouldFallbackToDefaultTerminalUseCaseImpl,
    ): ShouldFallbackToDefaultTerminalUseCase

    @Binds
    fun bindGetSelectedTerminalTypeUseCase(
        impl: GetSelectedTerminalTypeUseCaseImpl,
    ): GetSelectedTerminalTypeUseCase

    @Binds
    fun bindGetSelectedTerminalTypeFlowUseCase(
        impl: GetSelectedTerminalTypeFlowUseCaseImpl,
    ): GetSelectedTerminalTypeFlowUseCase

    @Binds
    fun bindSetSelectedTerminalTypeUseCase(
        impl: SetSelectedTerminalTypeUseCaseImpl,
    ): SetSelectedTerminalTypeUseCase

    // Crashes Use Cases
    @Binds
    fun bindGetWrapCrashLogLinesUseCase(
        impl: GetWrapCrashLogLinesUseCaseImpl,
    ): GetWrapCrashLogLinesUseCase

    @Binds
    fun bindGetUseSeparateNotificationsChannelsForCrashesUseCase(
        impl: GetUseSeparateNotificationsChannelsForCrashesUseCaseImpl,
    ): GetUseSeparateNotificationsChannelsForCrashesUseCase

    @Binds
    fun bindGetOpenCrashesOnStartupUseCase(
        impl: GetOpenCrashesOnStartupUseCaseImpl,
    ): GetOpenCrashesOnStartupUseCase

    @Binds
    fun bindGetCrashesSortTypeFlowUseCase(
        impl: GetCrashesSortTypeFlowUseCaseImpl,
    ): GetCrashesSortTypeFlowUseCase

    @Binds
    fun bindSetCrashesSortTypeUseCase(
        impl: SetCrashesSortTypeUseCaseImpl,
    ): SetCrashesSortTypeUseCase

    @Binds
    fun bindGetCrashesSortReversedOrderFlowUseCase(
        impl: GetCrashesSortReversedOrderFlowUseCaseImpl,
    ): GetCrashesSortReversedOrderFlowUseCase

    @Binds
    fun bindSetCrashesSortReversedOrderUseCase(
        impl: SetCrashesSortReversedOrderUseCaseImpl,
    ): SetCrashesSortReversedOrderUseCase

    @Binds
    fun bindGetWrapCrashLogLinesFlowUseCase(
        impl: GetWrapCrashLogLinesFlowUseCaseImpl,
    ): GetWrapCrashLogLinesFlowUseCase

    @Binds
    fun bindGetUseSeparateNotificationsChannelsForCrashesFlowUseCase(
        impl: GetUseSeparateNotificationsChannelsForCrashesFlowUseCaseImpl,
    ): GetUseSeparateNotificationsChannelsForCrashesFlowUseCase

    // DateTime Use Cases
    @Binds
    fun bindGetDateFormatUseCase(
        impl: GetDateFormatUseCaseImpl,
    ): GetDateFormatUseCase

    @Binds
    fun bindGetDateFormatFlowUseCase(
        impl: GetDateFormatFlowUseCaseImpl,
    ): GetDateFormatFlowUseCase

    @Binds
    fun bindSetDateFormatUseCase(
        impl: SetDateFormatUseCaseImpl,
    ): SetDateFormatUseCase

    @Binds
    fun bindGetTimeFormatUseCase(
        impl: GetTimeFormatUseCaseImpl,
    ): GetTimeFormatUseCase

    @Binds
    fun bindGetTimeFormatFlowUseCase(
        impl: GetTimeFormatFlowUseCaseImpl,
    ): GetTimeFormatFlowUseCase

    @Binds
    fun bindSetTimeFormatUseCase(
        impl: SetTimeFormatUseCaseImpl,
    ): SetTimeFormatUseCase

    // UI Use Cases
    @Binds
    fun bindGetNightThemeUseCase(
        impl: GetNightThemeUseCaseImpl,
    ): GetNightThemeUseCase

    @Binds
    fun bindGetNightThemeFlowUseCase(
        impl: GetNightThemeFlowUseCaseImpl,
    ): GetNightThemeFlowUseCase

    @Binds
    fun bindSetNightThemeUseCase(
        impl: SetNightThemeUseCaseImpl,
    ): SetNightThemeUseCase

    // Notifications Use Cases
    @Binds
    fun bindGetAskedNotificationsPermissionUseCase(
        impl: GetAskedNotificationsPermissionUseCaseImpl,
    ): GetAskedNotificationsPermissionUseCase

    @Binds
    fun bindSetAskedNotificationsPermissionUseCase(
        impl: SetAskedNotificationsPermissionUseCaseImpl,
    ): SetAskedNotificationsPermissionUseCase

    // Service Use Cases
    @Binds
    fun bindGetStartOnBootUseCase(impl: GetStartOnBootUseCaseImpl): GetStartOnBootUseCase

    @Binds
    fun bindGetIncludeDeviceInfoInArchivesUseCase(
        impl: GetIncludeDeviceInfoInArchivesUseCaseImpl,
    ): GetIncludeDeviceInfoInArchivesUseCase
}
