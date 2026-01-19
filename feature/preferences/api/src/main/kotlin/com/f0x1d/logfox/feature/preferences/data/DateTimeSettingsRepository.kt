package com.f0x1d.logfox.feature.preferences.data

import kotlinx.coroutines.flow.Flow

interface DateTimeSettingsRepository {
    companion object {
        const val DATE_FORMAT_DEFAULT = "dd.MM"
        const val TIME_FORMAT_DEFAULT = "HH:mm:ss.SSS"
    }

    var dateFormat: String?
    val dateFormatFlow: Flow<String>

    var timeFormat: String?
    val timeFormatFlow: Flow<String>
}
