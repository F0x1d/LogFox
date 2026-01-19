package com.f0x1d.logfox.feature.preferences.impl.data.crashes

import android.content.Context
import com.f0x1d.logfox.feature.preferences.CrashesSort
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import com.fredporciuncula.flow.preferences.Preference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CrashesSettingsLocalDataSourceImpl
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : BasePreferences(context),
        CrashesSettingsLocalDataSource {
        override var openCrashesOnStartup
            get() = get(KEY_OPEN_CRASHES_ON_STARTUP, false)
            set(value) = put(KEY_OPEN_CRASHES_ON_STARTUP, value)

        override var wrapCrashLogLines
            get() = get(KEY_WRAP_CRASH_LOG_LINES, true)
            set(value) = put(KEY_WRAP_CRASH_LOG_LINES, value)

        override val crashesSortType: Preference<CrashesSort>
            get() =
                flowSharedPreferences.getEnum(
                    key = KEY_CRASHES_SORT_TYPE,
                    defaultValue = CrashesSort.NEW,
                )

        override val crashesSortReversedOrder: Preference<Boolean>
            get() =
                flowSharedPreferences.getBoolean(
                    key = KEY_CRASHES_SORT_REVERSED_ORDER,
                    defaultValue = true,
                )

        override fun updateCrashesSortSettings(
            sortType: CrashesSort,
            sortInReversedOrder: Boolean,
        ) {
            put(KEY_CRASHES_SORT_TYPE, sortType.name)
            put(KEY_CRASHES_SORT_REVERSED_ORDER, sortInReversedOrder)
        }

        override fun collectingFor(crashTypeName: String) =
            get(
                key = "pref_collect_${crashTypeName.lowercase()}",
                defaultValue = true,
            )

        override fun showingNotificationsFor(crashTypeName: String) =
            get(
                key = "pref_notifications_${crashTypeName.lowercase()}",
                defaultValue = true,
            )

        override var useSeparateNotificationsChannelsForCrashes
            get() = get(KEY_USE_SEPARATE_NOTIFICATIONS_CHANNELS, true)
            set(value) = put(KEY_USE_SEPARATE_NOTIFICATIONS_CHANNELS, value)

        private companion object {
            const val KEY_OPEN_CRASHES_ON_STARTUP = "pref_open_crashes_page_on_startup"
            const val KEY_WRAP_CRASH_LOG_LINES = "pref_wrap_crash_log_lines"
            const val KEY_CRASHES_SORT_TYPE = "pref_crashes_sort_type"
            const val KEY_CRASHES_SORT_REVERSED_ORDER = "pref_crashes_sort_reversed_order"
            const val KEY_USE_SEPARATE_NOTIFICATIONS_CHANNELS = "pref_notifications_use_separate_channels"
        }
    }
