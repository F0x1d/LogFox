package com.f0x1d.logfox.feature.crashes.presentation.details

import com.f0x1d.logfox.feature.crashes.api.model.AppCrash

internal data class CrashDetailsViewState(
    val crash: AppCrash?,
    val crashLog: String?,
    val blacklisted: Boolean?,
    val wrapCrashLogLines: Boolean,
    val useSeparateNotificationsChannelsForCrashes: Boolean,
    val searchQuery: String,
    val searchMatchRanges: List<IntRange>,
)
