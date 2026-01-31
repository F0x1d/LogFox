package com.f0x1d.logfox.feature.logging.impl.di

import com.f0x1d.logfox.feature.logging.impl.data.LogExportDataSource
import com.f0x1d.logfox.feature.logging.impl.data.LogExportDataSourceImpl
import com.f0x1d.logfox.feature.logging.impl.data.LogsBufferDataSource
import com.f0x1d.logfox.feature.logging.impl.data.LogsBufferDataSourceImpl
import com.f0x1d.logfox.feature.logging.impl.data.LogsDataSource
import com.f0x1d.logfox.feature.logging.impl.data.LogsDataSourceImpl
import com.f0x1d.logfox.feature.logging.impl.data.SearchDataSource
import com.f0x1d.logfox.feature.logging.impl.data.SearchDataSourceImpl
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
    fun bindSearchDataSource(searchDataSourceImpl: SearchDataSourceImpl): SearchDataSource

    @Binds
    fun bindSelectedLogLinesDataSource(
        selectedLogLinesDataSourceImpl: SelectedLogLinesDataSourceImpl,
    ): SelectedLogLinesDataSource

    @Binds
    fun bindLogExportDataSource(impl: LogExportDataSourceImpl): LogExportDataSource
}
