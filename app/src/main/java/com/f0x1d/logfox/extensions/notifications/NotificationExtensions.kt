package com.f0x1d.logfox.extensions.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import com.f0x1d.logfox.R

fun NotificationCompat.Builder.applyPrimaryColor(context: Context): NotificationCompat.Builder {
    color = context.getColor(R.color.color_primary)
    return this
}