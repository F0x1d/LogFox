package com.f0x1d.logfox.preferences.shared.crashes

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.f0x1d.logfox.database.entity.AppCrashesCount
import com.f0x1d.logfox.strings.Strings

@Keep
enum class CrashesSort(
    @StringRes val titleRes: Int,
    val sorter: (List<AppCrashesCount>) -> List<AppCrashesCount> = { it },
) {
    NAME(
        titleRes = Strings.sort_by_name,
        sorter = { crashes ->
            crashes.sortedBy { it.lastCrash.appName ?: it.lastCrash.packageName }
        },
    ),
    NEW(
        titleRes = Strings.sort_by_new,
        sorter = { crashes ->
            crashes.sortedByDescending { it.lastCrash.dateAndTime }
        },
    ),
    COUNT(
        titleRes = Strings.sort_by_count,
        sorter = { crashes ->
            crashes.sortedByDescending { it.count }
        },
    ),
}
