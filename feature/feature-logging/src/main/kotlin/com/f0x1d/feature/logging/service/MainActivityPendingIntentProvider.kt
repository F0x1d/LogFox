package com.f0x1d.feature.logging.service

import android.app.PendingIntent

interface MainActivityPendingIntentProvider {
    fun provide(id: Int): PendingIntent
}
