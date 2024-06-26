package com.f0x1d.logfox.extensions.context

import android.content.Context
import com.f0x1d.logfox.feature.logging.core.repository.LoggingRepository
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import io.github.inflationx.viewpump.ViewPump

val Context.appPreferences get() = extensionsEntryPoint.appPreferences()
//val Context.dateTimeFormatter get() = extensionsEntryPoint.dateTimeFormatter()
val Context.viewPump get() = extensionsEntryPoint.viewPump()
val Context.loggingRepository get() = extensionsEntryPoint.loggingRepository()

private val Context.extensionsEntryPoint get() = EntryPointAccessors.fromApplication(
    this,
    ContextExtensionsEntryPoint::class.java
)

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface ContextExtensionsEntryPoint {
    fun appPreferences(): AppPreferences
    //fun dateTimeFormatter(): com.f0x1d.logfox.feature.datetime.DateTimeFormatter
    fun viewPump(): ViewPump
    fun loggingRepository(): LoggingRepository
}
