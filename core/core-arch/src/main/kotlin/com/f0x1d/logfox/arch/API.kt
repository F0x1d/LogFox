package com.f0x1d.logfox.arch

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Build.VERSION_CODES.Q
import android.os.Build.VERSION_CODES.R
import android.os.Build.VERSION_CODES.S
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.annotation.ChecksSdkIntAtLeast

@get:ChecksSdkIntAtLeast(api = TIRAMISU) val isAtLeastAndroid13 = SDK_INT >= TIRAMISU

@get:ChecksSdkIntAtLeast(api = Q) val gesturesAvailable = SDK_INT >= Q
@get:ChecksSdkIntAtLeast(api = O) val contrastedNavBarAvailable = SDK_INT >= O

@get:ChecksSdkIntAtLeast(api = S) val notificationsDynamicColorAvailable = SDK_INT >= S
@get:ChecksSdkIntAtLeast(api = O) val notificationsChannelsAvailable = SDK_INT >= O
@get:ChecksSdkIntAtLeast(api = TIRAMISU) val shouldRequestNotificationsPermission = SDK_INT >= TIRAMISU
@get:ChecksSdkIntAtLeast(api = O) val startForegroundServiceAvailable = notificationsChannelsAvailable

@get:ChecksSdkIntAtLeast(api = R) val canPickJSON = SDK_INT >= R

@get:ChecksSdkIntAtLeast(api = S) val uiModeManagerAvailable = SDK_INT >= S
@get:ChecksSdkIntAtLeast(api = S) val mutablePendingIntentAvailable = uiModeManagerAvailable
