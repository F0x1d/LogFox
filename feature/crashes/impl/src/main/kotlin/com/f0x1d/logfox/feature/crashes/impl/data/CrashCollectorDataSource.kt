package com.f0x1d.logfox.feature.crashes.impl.data

import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import com.f0x1d.logfox.feature.logging.api.model.LogLine

internal interface CrashCollectorDataSource {
    suspend fun collectCrash(appCrash: AppCrash, logLines: List<LogLine>)
}
