package com.f0x1d.logfox.datetime

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DateTimeFormatterModule {

    @Binds
    fun bindDateTimeFormatter(
        dateTimeFormatterImpl: DateTimeFormatterImpl,
    ): DateTimeFormatter
}
