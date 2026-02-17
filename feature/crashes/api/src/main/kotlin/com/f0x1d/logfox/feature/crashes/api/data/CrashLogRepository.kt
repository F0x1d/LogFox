package com.f0x1d.logfox.feature.crashes.api.data

import com.f0x1d.logfox.feature.crashes.api.model.AppCrash

interface CrashLogRepository {
    fun readCrashLog(appCrash: AppCrash): List<String>
}
