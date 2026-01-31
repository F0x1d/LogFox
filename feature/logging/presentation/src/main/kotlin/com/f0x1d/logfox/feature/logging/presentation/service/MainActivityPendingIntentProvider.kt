package com.f0x1d.logfox.feature.logging.presentation.service

import android.app.PendingIntent

interface MainActivityPendingIntentProvider {
    fun provide(id: Int): PendingIntent
}
