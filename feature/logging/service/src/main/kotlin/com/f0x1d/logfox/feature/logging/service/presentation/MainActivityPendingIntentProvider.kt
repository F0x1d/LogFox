package com.f0x1d.logfox.feature.logging.service.presentation

import android.app.PendingIntent

interface MainActivityPendingIntentProvider {
    fun provide(id: Int): PendingIntent
}
