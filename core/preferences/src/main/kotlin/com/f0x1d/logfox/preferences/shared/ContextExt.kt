package com.f0x1d.logfox.preferences.shared

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

val Context.appPreferences get() = EntryPointAccessors
    .fromApplication<AppPreferencesEntryPoint>(this)
    .appPreferences

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface AppPreferencesEntryPoint {
    val appPreferences: AppPreferences
}
