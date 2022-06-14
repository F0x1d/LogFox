package com.f0x1d.logfox.extensions

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.f0x1d.logfox.R
import com.f0x1d.logfox.repository.LoggingRepository
import com.f0x1d.logfox.service.LoggingService
import kotlin.system.exitProcess

fun Context.copyText(text: String) = (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
    .setPrimaryClip(ClipData.newPlainText("FoxCat", text))
    .apply {
        toast(R.string.text_copied)
    }

fun Context.hasPermissionToReadLogs() = ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.READ_LOGS
) == PackageManager.PERMISSION_GRANTED

val Context.notificationManagerCompat
    get() = NotificationManagerCompat.from(this)
val Context.activityManager
    get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

fun <T : BroadcastReceiver> Context.makeBroadcastPendingIntent(id: Int, clazz: Class<T>, setup: Intent.() -> Unit) = PendingIntent.getBroadcast(
    this,
    id,
    Intent(this, clazz).also { setup.invoke(it) },
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
)

fun <T : BroadcastReceiver> Context.makeBroadcastPendingIntent(id: Int, clazz: Class<T>, extras: Bundle = Bundle.EMPTY) = makeBroadcastPendingIntent(
    id,
    clazz
) { putExtras(extras) }

fun <T : Service> Context.makeServicePendingIntent(id: Int, clazz: Class<T>, setup: Intent.() -> Unit) = PendingIntent.getService(
    this,
    id,
    Intent(this, clazz).also { setup.invoke(it) },
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
)

fun <T : Service> Context.makeServicePendingIntent(id: Int, clazz: Class<T>, extras: Bundle = Bundle.EMPTY) = makeServicePendingIntent(
    id,
    clazz
) { putExtras(extras) }

fun <T : Activity> Context.makeActivityPendingIntent(id: Int, clazz: Class<T>, setup: Intent.() -> Unit) = PendingIntent.getActivity(
    this,
    id,
    Intent(this, clazz).also { setup.invoke(it) },
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
)

fun <T : Activity> Context.makeActivityPendingIntent(id: Int, clazz: Class<T>, extras: Bundle = Bundle.EMPTY) = makeActivityPendingIntent(
    id,
    clazz
) { putExtras(extras) }

fun Context.startLoggingAndService(loggingRepository: LoggingRepository) {
    loggingRepository.startLoggingIfNot()

    Intent(this, LoggingService::class.java).also {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(it)
        else
            startService(it)
    }
}

fun Context.startLoggingAndServiceIfCan(loggingRepository: LoggingRepository) {
    if (hasPermissionToReadLogs()) {
        startLoggingAndService(loggingRepository)
    }
}

fun Context.hardRestartApp() {
    for (task in activityManager.appTasks) task.finishAndRemoveTask()

    val intent = packageManager.getLaunchIntentForPackage(packageName)
    startActivity(intent)
    exitProcess(0)
}

fun Context.toast(text: Int) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()