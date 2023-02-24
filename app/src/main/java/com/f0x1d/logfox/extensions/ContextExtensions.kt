package com.f0x1d.logfox.extensions

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.f0x1d.logfox.R
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.service.LoggingService
import com.f0x1d.logfox.utils.preferences.AppPreferences
import java.io.File
import kotlin.system.exitProcess


fun Context.copyText(text: String) = (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
    .setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name), text))

fun Context.hasPermissionToReadLogs() = ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.READ_LOGS
) == PackageManager.PERMISSION_GRANTED

val Context.notificationManagerCompat get() = NotificationManagerCompat.from(this)
val Context.notificationManager get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
val Context.activityManager get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

fun Context.startLoggingAndService(loggingRepository: LoggingRepository, appPreferences: AppPreferences, force: Boolean = false) {
    loggingRepository.startLoggingIfNot()

    if (appPreferences.startOnLaunch || force) {
        startLoggingService()
    }
}

fun Context.startLoggingAndServiceIfCan(loggingRepository: LoggingRepository, appPreferences: AppPreferences, force: Boolean = false) {
    if (hasPermissionToReadLogs()) {
        startLoggingAndService(loggingRepository, appPreferences, force)
    }
}

fun Context.startLoggingService() {
    Intent(this, LoggingService::class.java).also {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(it)
        else
            startService(it)
    }
}

fun Context.hardRestartApp() {
    for (task in activityManager.appTasks) task.finishAndRemoveTask()

    val intent = packageManager.getLaunchIntentForPackage(packageName)
    startActivity(intent)
    exitProcess(0)
}

fun Context.toast(text: Int) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun Context.shareIntent(text: String) = baseShareIntent {
    it.putExtra(Intent.EXTRA_TEXT, text)
    it.type = "text/plain"
}

fun Context.shareFileIntent(file: File) = baseShareIntent {
    val uri = FileProvider.getUriForFile(this, "com.f0x1d.logfox.provider", file)

    it.putExtra(Intent.EXTRA_STREAM, uri)
    it.type = "text/plain"
}

private fun Context.baseShareIntent(block: (Intent) -> Unit) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        block.invoke(intent)

        startActivity(Intent.createChooser(intent, getString(R.string.share)))
    } catch (e: Exception) {
        e.printStackTrace()
        toast(R.string.too_big_log)
    }
}

fun Context.catchingNotNumber(block: () -> Unit) = try {
    block.invoke()
} catch (e: NumberFormatException) {
    toast(R.string.this_is_not_a_number)
}

fun Context.sendKillApp() = sendService(LoggingService.ACTION_KILL_SERVICE)
fun Context.sendStopService() = sendService(LoggingService.ACTION_STOP_SERVICE)
private fun Context.sendService(action: String) = startService(Intent(this, LoggingService::class.java).setAction(action))

fun Context.hasNotificationsPermission() = if (Build.VERSION.SDK_INT >= 33)
    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
else
    true

fun Context.doIfPermitted(block: NotificationManagerCompat.() -> Unit) = if (hasNotificationsPermission())
    block.invoke(notificationManagerCompat)
else
    Unit