package com.f0x1d.logfox.extensions

import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.f0x1d.logfox.utils.pendingIntentFlags

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