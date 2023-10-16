package com.f0x1d.logfox.extensions.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.notificationsDynamicColorAvailable

fun NotificationCompat.Builder.applyPrimaryColorIfNeed(context: Context): NotificationCompat.Builder {
    if (!notificationsDynamicColorAvailable) color = context.getColor(R.color.color_primary)
    return this
}