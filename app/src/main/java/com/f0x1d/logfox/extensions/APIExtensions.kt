package com.f0x1d.logfox.extensions

import android.os.Build.VERSION.*
import android.os.Build.VERSION_CODES.*
import androidx.annotation.ChecksSdkIntAtLeast

@get:ChecksSdkIntAtLeast(api = TIRAMISU) val isAtLeastAndroid13 = SDK_INT >= TIRAMISU

@get:ChecksSdkIntAtLeast(api = Q) val gesturesAvailable = SDK_INT >= Q
@get:ChecksSdkIntAtLeast(api = O_MR1) val contrastedNavBarAvailable = SDK_INT >= O_MR1

@get:ChecksSdkIntAtLeast(api = S) val notificationsDynamicColorAvailable = SDK_INT >= S
@get:ChecksSdkIntAtLeast(api = O) val notificationsChannelsAvailable = SDK_INT >= O
@get:ChecksSdkIntAtLeast(api = TIRAMISU) val shouldRequestNotificationsPermission = SDK_INT >= TIRAMISU
@get:ChecksSdkIntAtLeast(api = O) val startForegroundServiceAvailable = notificationsChannelsAvailable

@get:ChecksSdkIntAtLeast(api = R) val canPickJSON = SDK_INT >= R

@get:ChecksSdkIntAtLeast(api = S) val uiModeManagerAvailable = SDK_INT >= S
@get:ChecksSdkIntAtLeast(api = S) val mutablePendingIntentAvailable = uiModeManagerAvailable