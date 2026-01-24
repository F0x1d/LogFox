package com.f0x1d.logfox.feature.logging.impl.di

import com.f0x1d.logfox.feature.logging.impl.data.LogsBufferDataSource
import com.f0x1d.logfox.feature.logging.impl.data.LogsBufferDataSourceImpl
import com.f0x1d.logfox.feature.logging.impl.data.LogsDataSource
import com.f0x1d.logfox.feature.logging.impl.data.LogsDataSourceImpl
import com.f0x1d.logfox.feature.logging.impl.data.PausedDataSource
import com.f0x1d.logfox.feature.logging.impl.data.PausedDataSourceImpl
import com.f0x1d.logfox.feature.logging.impl.data.QueryDataSource
import com.f0x1d.logfox.feature.logging.impl.data.QueryDataSourceImpl
import com.f0x1d.logfox.feature.logging.impl.data.SelectedLogLinesDataSource
import com.f0x1d.logfox.feature.logging.impl.data.SelectedLogLinesDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataSourcesModule {

    @Binds
    fun bindLogsBufferDataSource(logsBufferDataSourceImpl: LogsBufferDataSourceImpl): LogsBufferDataSource

    @Binds
    fun bindLogsDataSource(logsDataSourceImpl: LogsDataSourceImpl): LogsDataSource

    @Binds
    fun bindQueryDataSource(queryDataSourceImpl: QueryDataSourceImpl): QueryDataSource

    @Binds
    fun bindSelectedLogLinesDataSource(
        selectedLogLinesDataSourceImpl: SelectedLogLinesDataSourceImpl,
    ): SelectedLogLinesDataSource

    @Binds
    fun bindPausedDataSource(pausedDataSourceImpl: PausedDataSourceImpl): PausedDataSource
}
