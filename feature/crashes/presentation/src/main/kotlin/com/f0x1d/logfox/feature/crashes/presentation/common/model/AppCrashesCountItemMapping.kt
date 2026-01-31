package com.f0x1d.logfox.feature.crashes.presentation.common.model

import com.f0x1d.logfox.feature.crashes.api.model.AppCrashesCount

fun AppCrashesCount.toPresentationModel(formattedDate: String) = AppCrashesCountItem(
    lastCrashId = lastCrash.id,
    appName = lastCrash.appName,
    packageName = lastCrash.packageName,
    crashType = lastCrash.crashType,
    count = count,
    formattedDate = formattedDate,
)
