package com.f0x1d.logfox.datetime

import android.content.Context
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
