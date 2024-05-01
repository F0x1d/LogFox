package com.f0x1d.logfox.extensions.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import com.f0x1d.logfox.extensions.notificationsDynamicColorAvailable

fun NotificationCompat.Builder.applyPrimaryColorIfNeeded(context: Context) = apply {
    if (!notificationsDynamicColorAvailable) color = context.getColor(
        com.f0x1d.logfox.ui.R.color.color_primary
    )
}
