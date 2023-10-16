package com.f0x1d.logfox.extensions

import android.os.Build

val isAtLeastAndroid13 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

val gesturesAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
val contrastedNavBarAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

val notificationsDynamicColorAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
val notificationsChannelsAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
val shouldRequestNotificationsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
val startForegroundServiceAvailable = notificationsChannelsAvailable

val canPickJSON = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

val uiModeManagerAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
val mutablePendingIntentAvailable = uiModeManagerAvailable