package com.f0x1d.logfox.feature.preferences.impl.data.crashes

import android.content.Context
import com.f0x1d.logfox.feature.preferences.api.CrashesSort
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import com.fredporciuncula.flow.preferences.Preference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CrashesSettingsLocalDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context,
) : BasePreferences(context),
    CrashesSettingsLocalDataSource {

    override fun openCrashesOnStartup(): Preference<Boolean> = booleanPreference(
        key = KEY_OPEN_CRASHES_ON_STARTUP,
        defaultValue = false,
    )

    override fun wrapCrashLogLines(): Preference<Boolean> = booleanPreference(
        key = KEY_WRAP_CRASH_LOG_LINES,
        defaultValue = true,
    )

    override fun crashesSortType(): Preference<CrashesSort> = enumPreference(
        key = KEY_CRASHES_SORT_TYPE,
        defaultValue = CrashesSort.NEW,
    )

    override fun crashesSortReversedOrder(): Preference<Boolean> = booleanPreference(
        key = KEY_CRASHES_SORT_REVERSED_ORDER,
        defaultValue = true,
    )

    override fun collectingFor(crashTypeName: String): Boolean = booleanPreference(
        key = "pref_collect_${crashTypeName.lowercase()}",
        defaultValue = true,
    ).get()

    override fun showingNotificationsFor(crashTypeName: String): Boolean = booleanPreference(
        key = "pref_notifications_${crashTypeName.lowercase()}",
        defaultValue = true,
    ).get()

    override fun useSeparateNotificationsChannelsForCrashes(): Preference<Boolean> = booleanPreference(
        key = KEY_USE_SEPARATE_NOTIFICATIONS_CHANNELS,
        defaultValue = true,
    )

    private companion object {
        const val KEY_OPEN_CRASHES_ON_STARTUP = "pref_open_crashes_page_on_startup"
        const val KEY_WRAP_CRASH_LOG_LINES = "pref_wrap_crash_log_lines"
        const val KEY_CRASHES_SORT_TYPE = "pref_crashes_sort_type"
        const val KEY_CRASHES_SORT_REVERSED_ORDER = "pref_crashes_sort_reversed_order"
        const val KEY_USE_SEPARATE_NOTIFICATIONS_CHANNELS = "pref_notifications_use_separate_channels"
    }
}
