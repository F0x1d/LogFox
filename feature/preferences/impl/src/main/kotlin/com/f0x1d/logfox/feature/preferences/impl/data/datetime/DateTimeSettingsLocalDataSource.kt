package com.f0x1d.logfox.feature.preferences.impl.data.datetime

import kotlinx.coroutines.flow.Flow

internal interface DateTimeSettingsLocalDataSource {
    var dateFormat: String?
    val dateFormatFlow: Flow<String>

    var timeFormat: String?
    val timeFormatFlow: Flow<String>
}
