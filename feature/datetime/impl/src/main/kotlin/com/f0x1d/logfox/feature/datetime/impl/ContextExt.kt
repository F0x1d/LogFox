package com.f0x1d.logfox.feature.datetime.impl

import android.content.Context
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

val Context.dateTimeFormatter get() = EntryPointAccessors
    .fromApplication<DateTimeFormatterEntryPoint>(this)
    .dateTimeFormatter

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface DateTimeFormatterEntryPoint {
    val dateTimeFormatter: DateTimeFormatter
}
