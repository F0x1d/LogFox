package com.f0x1d.logfox.extensions

import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.f0x1d.logfox.ui.activity.MainActivity

const val CRASH_DETAILS_INTENT_ID = 0
const val COPY_CRASH_INTENT_ID = 1
const val STOP_LOGGING_SERVICE_INTENT_ID = 2
const val EXIT_APP_INTENT_ID = 3
const val PAUSE_RECORDING_INTENT_ID = 4
const val RESUME_RECORDING_INTENT_ID = 5
const val STOP_RECORDING_INTENT_ID = 6
const val OPEN_APP_INTENT_ID = 7
const val SHIZUKU_PERMISSION_REQUEST_ID = 8

val pendingIntentFlags = if (mutablePendingIntentAvailable)
    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
else
    PendingIntent.FLAG_UPDATE_CURRENT

fun <T : BroadcastReceiver> Context.makeBroadcastPendingIntent(id: Int, clazz: Class<T>, setup: Intent.() -> Unit) = PendingIntent.getBroadcast(
    this,
    id,
    Intent(this, clazz).also { setup.invoke(it) },
    pendingIntentFlags
)

fun <T : BroadcastReceiver> Context.makeBroadcastPendingIntent(id: Int, clazz: Class<T>, extras: Bundle = Bundle.EMPTY) = makeBroadcastPendingIntent(
    id,
    clazz
) { putExtras(extras) }

fun <T : Service> Context.makeServicePendingIntent(id: Int, clazz: Class<T>, setup: Intent.() -> Unit) = PendingIntent.getService(
    this,
    id,
    Intent(this, clazz).also { setup.invoke(it) },
    pendingIntentFlags
)

fun <T : Service> Context.makeServicePendingIntent(id: Int, clazz: Class<T>, extras: Bundle = Bundle.EMPTY) = makeServicePendingIntent(
    id,
    clazz
) { putExtras(extras) }

fun <T : Activity> Context.makeActivityPendingIntent(id: Int, clazz: Class<T>, setup: Intent.() -> Unit) = PendingIntent.getActivity(
    this,
    id,
    Intent(this, clazz).also { setup.invoke(it) },
    pendingIntentFlags
)

fun <T : Activity> Context.makeActivityPendingIntent(id: Int, clazz: Class<T>, extras: Bundle = Bundle.EMPTY) = makeActivityPendingIntent(
    id,
    clazz
) { putExtras(extras) }

fun Context.makeOpenAppPendingIntent() = makeActivityPendingIntent(OPEN_APP_INTENT_ID, MainActivity::class.java)